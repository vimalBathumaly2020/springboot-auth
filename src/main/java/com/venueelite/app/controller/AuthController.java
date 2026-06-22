package com.venueelite.app.controller;

import com.venueelite.app.dto.*;
import com.venueelite.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import com.venueelite.app.dto.*;
import com.venueelite.app.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ========================= REGISTER =========================
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    // ========================= LOGIN =========================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ========================= REFRESH TOKEN =========================
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    // ========================= LOGOUT =========================
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(
                MessageResponse.builder()
                        .message("Logged out successfully")
                        .success(true)
                        .build()
        );
    }

    // ========================= FORGOT PASSWORD =========================
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {

        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = httpRequest.getRemoteAddr();

        authService.forgotPassword(request, ip);
        return ResponseEntity.ok(
                MessageResponse.builder()
                        .message("Reset link sent to email")
                        .success(true)
                        .build()
        );
    }

    // ========================= RESET PASSWORD =========================
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {

        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = httpRequest.getRemoteAddr();

        authService.resetPassword(request, ip);
        return ResponseEntity.ok(
                MessageResponse.builder()
                        .message("Password reset successful")
                        .success(true)
                        .build()
        );
    }

}