package com.venueelite.app.entity;


import com.venueelite.app.enums.Role;
import com.venueelite.app.enums.UserStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;


@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    private String fullName;

    @Indexed(unique = true)
    private String email;

    private String password;

    private Role role;

    private String profileImage;

    private UserStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;




}
