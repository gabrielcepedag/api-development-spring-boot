package com.darvybm.project.apidevelopment.repository;

import com.darvybm.project.apidevelopment.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends MongoRepository<Product, UUID> {
    List<Product> findAllByDeletedFalse();
    Optional<Product> findByIdAndDeletedFalse(UUID id);
}