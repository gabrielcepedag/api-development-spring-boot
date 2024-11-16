package com.darvybm.project.apidevelopment.controller.api.v1;

import com.darvybm.project.apidevelopment.dto.request.ProductRequest;
import com.darvybm.project.apidevelopment.dto.response.ProductResponse;
import com.darvybm.project.apidevelopment.dto.response.UserResponse;
import com.darvybm.project.apidevelopment.model.Product;
import com.darvybm.project.apidevelopment.service.impl.ProductServiceImpl;
import com.darvybm.project.apidevelopment.utils.response.CustResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;
    private final ModelMapper modelMapper;
    private final CustResponseBuilder custResponseBuilder;

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product savedProduct = productService.save(productRequest);
        ProductResponse productResponse = modelMapper.map(savedProduct, ProductResponse.class);
        return custResponseBuilder.ok(productResponse, "Product created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable UUID id) {
        Product product = productService.getById(id);
        ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
        return custResponseBuilder.ok(productResponse);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.getAll();
        List<ProductResponse> productResponses = Arrays.asList(modelMapper.map(products, ProductResponse[].class));
        return custResponseBuilder.ok(productResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequest productRequest) {
        Product updatedProduct = productService.update(id, productRequest);
        ProductResponse productResponse = modelMapper.map(updatedProduct, ProductResponse.class);
        return custResponseBuilder.ok(productResponse, "Product updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        productService.delete(id);
        return custResponseBuilder.ok("Product deleted successfully");
    }
}