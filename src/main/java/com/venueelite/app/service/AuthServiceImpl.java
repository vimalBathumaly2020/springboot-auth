package com.venueelite.app.service;

import com.venueelite.app.dto.*;
import com.venueelite.app.entity.PasswordResetToken;
import com.venueelite.app.entity.RefreshToken;
import com.venueelite.app.entity.User;
import com.venueelite.app.enums.Role;
import com.venueelite.app.enums.UserStatus;
import com.venueelite.app.exception.*;
import com.venueelite.app.repository.PasswordResetTokenRepository;
import com.venueelite.app.repository.RefreshTokenRepository;
import com.venueelite.app.repository.UserRepository;
import com.venueelite.app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;


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
            throw new EmailAlreadyExistsException("Email already exists");
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
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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
    public void forgotPassword(ForgotPasswordRequest request, String ipAddress) {

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();

        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        String hashedToken = DigestUtils.sha256Hex(rawToken);

        passwordResetTokenRepository.deleteByEmail(user.getEmail());

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(user.getEmail())
                .token(hashedToken)
                .ipAddress(ipAddress) // ← store IP
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
    }

    // ================= RESET PASSWORD =================
    @Override
    public void resetPassword(ResetPasswordRequest request, String ipAddress) {

        String hashedToken = DigestUtils.sha256Hex(request.getToken());

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(hashedToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("Token expired");
        }

        // verify IP matches
        if (!resetToken.getIpAddress().equals(ipAddress)) {
            throw new IpMismatchException("Token was not issued for this IP");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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