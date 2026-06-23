package com.venueelite.app.controller;

import com.venueelite.app.dto.ChangePasswordRequest;
import com.venueelite.app.dto.UpdateProfileRequest;
import com.venueelite.app.dto.UserProfileResponse;
import com.venueelite.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /users/profile
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication auth) {
        return ResponseEntity.ok(userService.getProfile(auth.getName()));
    }

    // PUT /users/profile
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication auth,
            @RequestPart(value = "data", required = false) UpdateProfileRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile) {

        if (request == null) request = new UpdateProfileRequest();
        return ResponseEntity.ok(userService.updateProfile(auth.getName(), request, avatarFile));
    }

    // PATCH /users/change-password
    @PatchMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<String> changePassword(
            Authentication auth,
            @RequestBody @Valid ChangePasswordRequest request) {

        userService.changePassword(auth.getName(), request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
