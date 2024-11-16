package com.darvybm.project.apidevelopment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Document(collection = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private UUID id;

    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Boolean isActive = true;
    private String supplier;
    private Category category;
    private Boolean deleted = false;
}