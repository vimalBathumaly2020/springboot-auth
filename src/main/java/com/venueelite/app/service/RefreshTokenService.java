package com.venueelite.app.service;

import com.venueelite.app.dto.AuthResponse;
import com.venueelite.app.entity.RefreshToken;
import com.venueelite.app.entity.User;
import com.venueelite.app.exception.InvalidTokenException;
import com.venueelite.app.exception.TokenExpiredException;
import com.venueelite.app.exception.UserNotFoundException;
import com.venueelite.app.repository.RefreshTokenRepository;
import com.venueelite.app.repository.UserRepository;
import com.venueelite.app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

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

        // generate cryptographically strong raw token
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        // hash before storing
        String hashedToken = DigestUtils.sha256Hex(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(hashedToken)                      // store hash
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(refreshToken);

        // return entity with RAW token so caller can send it to user
        refreshToken.setToken(rawToken);
        return refreshToken;
    }

    // ─── Rotate: validate old → issue new access + refresh ──────
    public AuthResponse rotateToken(String rawToken) {
        String hashedToken = DigestUtils.sha256Hex(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenExpiredException("Refresh token has expired, please login again");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // rotate: delete old, create new
        refreshTokenRepository.delete(refreshToken);
        RefreshToken newRefreshToken = createRefreshToken(user.getId());
        String newAccessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken()) // raw token returned to user
                .user(user)
                .build();
    }

    // ─── Logout: revoke token ────────────────────────────────────
    public void revokeToken(String rawToken) {
        String hashedToken = DigestUtils.sha256Hex(rawToken);
        refreshTokenRepository.deleteByToken(hashedToken);
    }
}