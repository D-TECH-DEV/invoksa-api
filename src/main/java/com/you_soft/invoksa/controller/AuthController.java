package com.you_soft.invoksa.controller;

import com.you_soft.invoksa.config.JwtUtils;
import com.you_soft.invoksa.dto.request.LoginRequest;
import com.you_soft.invoksa.entity.EmailVerificationToken;
import com.you_soft.invoksa.entity.User;
import com.you_soft.invoksa.repository.EmailVerificationTokenRepository;
import com.you_soft.invoksa.repository.UserRepository;
import com.you_soft.invoksa.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cet email est déjà utilisé"));
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Nom d'utilisateur déjà utilisé"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        // Créer le token de vérification email
        String tokenEmailCheck = UUID.randomUUID().toString();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(tokenEmailCheck);
        token.setUser(savedUser);
        token.setExpirationDate(
                new Date(System.currentTimeMillis() + 1000 * 60 * 15));

        emailVerificationTokenRepository.save(token);

        emailService.sendEmail(
                savedUser.getEmail(),
                "Vérification de votre compte",
                "Cliquez sur ce lien pour vérifier votre compte : " +
                        "http://localhost:8080/api/auth/verify?token=" + tokenEmailCheck +
                        "\n\nVous avez 15 minutes pour confirmer votre email !");

        return ResponseEntity.ok("Email de vérification envoyé à " + savedUser.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // On authentifie avec l'email comme "username" — Spring appellera
            // CustomUerDetailsService.loadUserByUsername(email)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                // Le token JWT contient l'email comme subject
                authData.put("token", "Bearer " + jwtUtils.generateJwtToken(loginRequest.getEmail()));
                authData.put("email", loginRequest.getEmail());
                return ResponseEntity.ok(authData);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email ou mot de passe incorrect"));

        } catch (AuthenticationException e) {
            log.error("Erreur d'authentification pour {} : {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email ou mot de passe incorrect : " + e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {

        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token);

        if (verificationToken == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Token invalide");
        }

        if (verificationToken.getExpirationDate().before(new Date())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Token expiré");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);

        return ResponseEntity.ok("Email vérifié avec succès. Vous pouvez maintenant vous connecter.");
    }

    @PostMapping("reset-pwd/{email}/{newPwd}")
    public ResponseEntity<?> resetPassword(@PathVariable String email, @PathVariable String newPwd) {

        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPwd));
            userRepository.save(user);
            return ResponseEntity.ok("Mot de passe modifié avec succès");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Aucun utilisateur avec cet email"));
    }
}
