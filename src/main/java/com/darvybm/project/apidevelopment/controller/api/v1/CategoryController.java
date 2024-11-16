package com.darvybm.project.apidevelopment.controller.api.v1;

import com.darvybm.project.apidevelopment.dto.request.CategoryRequest;
import com.darvybm.project.apidevelopment.model.Category;
import com.darvybm.project.apidevelopment.service.CategoryService;
import com.darvybm.project.apidevelopment.service.impl.CategoryServiceImpl;
import com.darvybm.project.apidevelopment.utils.response.CustResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceImpl categoryService;
    private final CustResponseBuilder custResponseBuilder;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = categoryService.getAll();
        return custResponseBuilder.ok(categories);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getCategoryById(@PathVariable UUID uuid) {
        Category category = categoryService.getById(uuid);
        return custResponseBuilder.ok(category);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryService.save(categoryRequest);
        return custResponseBuilder.ok(category, "The category was successfully created");
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryRequest categoryRequest, @PathVariable UUID uuid) {
        Category category = categoryService.update(uuid, categoryRequest);
        return custResponseBuilder.ok(category, "The category was successfully updated");
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID uuid) {
        categoryService.deleteById(uuid);
        return custResponseBuilder.ok(null, "Category was successfully deleted");
    }
}
