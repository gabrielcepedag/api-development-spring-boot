package com.darvybm.project.apidevelopment.repository;

import com.darvybm.project.apidevelopment.model.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<User, UUID> {
    List<User> findAllByDeletedFalse();

    Optional<User> findByIdAndDeletedFalse(UUID id);

    boolean existsByUsername(@NotEmpty(message = "Username cannot be empty or null") String username);
}