package com.you_soft.invoksa.service;

import com.you_soft.invoksa.config.JwtUtils;
import com.you_soft.invoksa.dto.request.LoginRequest;
import com.you_soft.invoksa.dto.request.RegisterRequest;
import com.you_soft.invoksa.dto.request.ResetPasswordRequest;
import com.you_soft.invoksa.dto.response.UserResponse;
import com.you_soft.invoksa.entity.EmailVerificationToken;
import com.you_soft.invoksa.entity.User;
import com.you_soft.invoksa.mapper.UserMapper;
import com.you_soft.invoksa.repository.EmailVerificationTokenRepository;
import com.you_soft.invoksa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserMapper userMapper;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Cette adresse email est déjà utilisée");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : "ROLE_USER")
                .emailVerified(false)
                .provider("local")
                .build();

        User savedUser = userRepository.save(user);
        String tokenEmailCheck = createToken(savedUser, "email_verify");

        try {
            emailService.sendEmail(
                    savedUser.getEmail(),
                    "Vérification de votre compte",
                    "Cliquez sur ce lien : " + baseUrl + "/api/auth/verify?token=" + tokenEmailCheck +
                            " (Vous avez 15 minutes pour confirmer le mail !)"
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de vérification: {}", e.getMessage());
        }

        return userMapper.toResponse(savedUser);
    }

    public Map<String, Object> login(LoginRequest request) {
        String identifier = request.getUsername();
        if (identifier == null || identifier.isEmpty()) {
            identifier = request.getEmail();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        identifier,
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User userEntity = userRepository.findByUsername(userDetails.getUsername());

        Map<String, Object> authData = new HashMap<>();
        authData.put("token", jwtUtils.generateJwtToken(userDetails));
        authData.put("user", userMapper.toResponse(userEntity));

        return authData;
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByTokenAndType(token, "email_verify");

        if (verificationToken == null) {
            throw new RuntimeException("Token invalide");
        }
        if (verificationToken.getExpirationDate().before(new Date())) {
            throw new RuntimeException("Token expiré");
        }
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        emailVerificationTokenRepository.delete(verificationToken);
    }

    public void confirmPasswordEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email incorrect !");
        }
        String tokenEmailCheck = createToken(user, "confirm_password");
        emailService.sendEmail(
                user.getEmail(),
                "Réinitialisation de mot de passe",
                "Cliquez sur ce lien pour réinitialiser votre mot de passe : " + baseUrl + "/api/auth/reset-pwd-form?token=" + tokenEmailCheck
        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("Aucun utilisateur n'a cet email");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private String createToken(User user, String tokenType) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(user);
        emailVerificationToken.setType(tokenType);
        emailVerificationToken.setExpirationDate(
                new Date(System.currentTimeMillis() + 1000 * 60 * 15)
        );
        emailVerificationTokenRepository.save(emailVerificationToken);
        return token;
    }
}
