package com.darvybm.project.apidevelopment.service.impl;

import com.darvybm.project.apidevelopment.dto.request.ProductRequest;
import com.darvybm.project.apidevelopment.exception.BadRequestException;
import com.darvybm.project.apidevelopment.exception.ResourceNotFoundException;
import com.darvybm.project.apidevelopment.model.Category;
import com.darvybm.project.apidevelopment.model.Product;
import com.darvybm.project.apidevelopment.repository.ProductRepository;
import com.darvybm.project.apidevelopment.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryServiceImpl categoryService;
    private final ModelMapper modelMapper;

    @Override
    public Product save(ProductRequest productRequest) {
        Category category = categoryService.getById(productRequest.getCategoryId());
        try {
            Product product = modelMapper.map(productRequest, Product.class);
            product.setId(UUID.randomUUID());
            product.setCategory(category);
            return productRepository.save(product);
        } catch (Exception e) {
            throw new BadRequestException("Error saving product", e.getMessage());
        }
    }

    @Override
    public Product getById(UUID id) {
        return findById(id);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAllByDeletedFalse();  // Solo obtiene usuarios activos
    }

    @Override
    public Product update(UUID id, ProductRequest productRequest) {
        Product product = findById(id);
        Category category = categoryService.getById(productRequest.getCategoryId());
        try {
            modelMapper.map(productRequest, product);
            product.setCategory(category);
            return productRepository.save(product);
        } catch (Exception e) {
            throw new BadRequestException("Error updating product", e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        Product product = findById(id);
        try {
            product.setDeleted(true);
            productRepository.save(product);
        } catch (Exception e) {
            throw new BadRequestException("Error deleting product", e.getMessage());
        }
    }

    private Product findById(UUID id) {
        return productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(Product.class.getSimpleName(), "id", id));
    }
}
