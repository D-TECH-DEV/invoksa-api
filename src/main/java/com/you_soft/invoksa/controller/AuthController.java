package com.you_soft.invoksa.controller;

import com.you_soft.invoksa.config.JwtUtils;
import com.you_soft.invoksa.dto.request.LoginRequest;
import com.you_soft.invoksa.dto.request.UserRequest;
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

        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Nom d'utilisateur déjà utilisé"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);   // BLOQUÉ
        // user.setFirstLogin(true);       // OTP requis plus tard

        User savedUser = userRepository.save(user);

        // crééer le token
        String tokenEmailCheck = UUID.randomUUID().toString();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(tokenEmailCheck);
        token.setUser(savedUser);
        token.setExpirationDate(
                new Date(System.currentTimeMillis() + 1000 * 60 * 15)
        );

        emailVerificationTokenRepository.save(token);

        emailService.sendEmail(
                savedUser.getEmail(),
                "Vérification de votre compte",

                "Cliquez sur ce lien : http://localhost:8080/api/auth/verify?token=" + tokenEmailCheck +
                        "vous avez 15 minutes pour confirmer le mail !"
        );


        return ResponseEntity.ok("Email de vérification envoyé");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", "Bearer " + jwtUtils.generateJwtToken(user.getUsername()));

                return ResponseEntity.ok(authData);
            }
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Username ou utilisateur incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Nom d'utilisateur ou mot de passe incorrect !");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByToken(token);

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

        return ResponseEntity.ok("Email vérifié avec succès");
    }

    @PostMapping("reset-pwd/{email}/{newPwd}")
    public ResponseEntity<?> resetPassword(@PathVariable String email, @PathVariable String newPwd) {

        User user = userRepository.findByEmail(email);
        if(user != null)  {
            user.setPassword(passwordEncoder.encode(newPwd));
            userRepository.save(user);
            return ResponseEntity.ok("Mot de passe modifié avec succès");
        }

        return ResponseEntity.ok("Aucun utilisateur n'a ce email");


    }

}
