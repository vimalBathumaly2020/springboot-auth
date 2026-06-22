package com.venueelite.app.service;

import com.venueelite.app.dto.ChangePasswordRequest;
import com.venueelite.app.dto.UpdateProfileRequest;
import com.venueelite.app.dto.UserProfileResponse;
import com.venueelite.app.entity.User;
import com.venueelite.app.enums.UserStatus;
import com.venueelite.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    // ─── GET Profile ────────────────────────────────────────────
    public UserProfileResponse getProfile(String email) {
        User user = findByEmail(email);
        return mapToResponse(user);
    }

    // ─── UPDATE Profile ─────────────────────────────────────────
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request,
                                             MultipartFile avatarFile) {
        User user = findByEmail(email);

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // Avatar upload via multipart (takes priority over URL string)
        if (avatarFile != null && !avatarFile.isEmpty()) {
            if (user.getProfileImage() != null) {
                cloudinaryService.deleteImage(user.getProfileImage()); // delete old
            }
            String url = cloudinaryService.uploadAvatar(avatarFile);
            user.setProfileImage(url);

        } else if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage()); // direct URL
        }

        userRepository.save(user);
        return mapToResponse(user);
    }

    // ─── CHANGE Password ────────────────────────────────────────
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must differ from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ─── Account Status Check ───────────────────────────────────
    public void checkAccountActive(User user) {
        if (user.getStatus() == UserStatus.BANNED) {
            throw new AccessDeniedException("Account is banned");
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new AccessDeniedException("Account is suspended");
        }
    }

    // ─── Helpers ────────────────────────────────────────────────
    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserProfileResponse mapToResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profileImage(user.getProfileImage())
                .role(user.getRole().name())
                .accountStatus(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}