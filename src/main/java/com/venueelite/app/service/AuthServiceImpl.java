package com.venueelite.app.service;

import com.venueelite.app.dto.*;
import com.venueelite.app.entity.PasswordResetToken;
import com.venueelite.app.entity.RefreshToken;
import com.venueelite.app.entity.User;
import com.venueelite.app.enums.Role;
import com.venueelite.app.enums.UserStatus;
import com.venueelite.app.repository.PasswordResetTokenRepository;
import com.venueelite.app.repository.RefreshTokenRepository;
import com.venueelite.app.repository.UserRepository;
import com.venueelite.app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    // ================= REGISTER =================
    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);  // ← save first

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        String accessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(user)
                .build();
    }

    // ================= LOGIN =================
    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        String accessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(user)
                .build();
    }

    // ================= REFRESH TOKEN =================
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.rotateToken(request.getRefreshToken());
    }

    // ================= LOGOUT =================
    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }


    // ================= FORGOT PASSWORD =================
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // generate token
        String token = UUID.randomUUID().toString();

        // save token in DB
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(user.getEmail())
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        passwordResetTokenRepository.save(resetToken);

        // send email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    // ================= RESET PASSWORD =================
    @Override
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    // ================= HELPER =================
    private AuthResponse buildAuthResponse(User user) {

        String accessToken = jwtService.generateToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}