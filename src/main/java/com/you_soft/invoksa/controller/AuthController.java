package com.you_soft.invoksa.controller;

import com.you_soft.invoksa.dto.request.LoginRequest;
import com.you_soft.invoksa.dto.request.RegisterRequest;
import com.you_soft.invoksa.dto.request.ResetPasswordRequest;
import com.you_soft.invoksa.dto.response.UserResponse;
import com.you_soft.invoksa.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
       return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email vérifié avec succès");
    }

    @PostMapping("/confirm-pwd/{email}")
    public ResponseEntity<String> confirmPasswordEmail(@PathVariable String email) {
        authService.confirmPasswordEmail(email);
        return ResponseEntity.ok("Mail de confirmation envoyé");
    }

    @PostMapping("/reset-pwd")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }
}
