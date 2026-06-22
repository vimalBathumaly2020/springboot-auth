package com.venueelite.app.repository;

import com.venueelite.app.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.venueelite.app.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}