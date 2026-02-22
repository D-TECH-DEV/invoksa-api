package com.you_soft.invoksa.controller;

import com.you_soft.invoksa.config.JwtUtils;
import com.you_soft.invoksa.entity.EmailVerificationToken;
import com.you_soft.invoksa.entity.User;
import com.you_soft.invoksa.repository.EmailVerificationTokenRepository;
import com.you_soft.invoksa.repository.UserRepository;
import com.you_soft.invoksa.service.AuthService;
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

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
       return authService.login(user);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping("confirm-pwd/{email}/")
    public ResponseEntity<?> confirmPasswordEmail(@PathVariable String email) {
        return authService.confirmPasswordEmail(email);
    }

    @PostMapping("reset-pwd/{email}/{newPwd}")
    public ResponseEntity<?> resetPassword(@PathVariable String email, @PathVariable String newPwd) {
        return authService.resetPassword(email, newPwd);
    }
}
