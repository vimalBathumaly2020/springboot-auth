package com.venueelite.app.service;

import com.venueelite.app.dto.AuthResponse;
import com.venueelite.app.entity.RefreshToken;
import com.venueelite.app.entity.User;
import com.venueelite.app.repository.RefreshTokenRepository;
import com.venueelite.app.repository.UserRepository;
import com.venueelite.app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    // ─── Create & Save Refresh Token ────────────────────────────
    public RefreshToken createRefreshToken(String userId) {
        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // ─── Rotate: validate old → issue new access + refresh ──────
    public AuthResponse rotateToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token has expired, please login again");
        }

        User user = userRepository.findById(refreshToken.getUserId())  // ← findById not findByEmail
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // rotate: delete old, create new
        refreshTokenRepository.delete(refreshToken);
        RefreshToken newRefreshToken = createRefreshToken(user.getId());  // ← pass id not email
        String newAccessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .user(user)
                .build();
    }

    // ─── Logout: revoke token ────────────────────────────────────
    public void revokeToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}