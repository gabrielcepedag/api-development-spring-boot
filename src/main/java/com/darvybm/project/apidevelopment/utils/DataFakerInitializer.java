package com.darvybm.project.apidevelopment.utils;

import com.darvybm.project.apidevelopment.dto.request.CategoryRequest;
import com.darvybm.project.apidevelopment.dto.request.ProductRequest;
import com.darvybm.project.apidevelopment.dto.request.UserRequest;
import com.darvybm.project.apidevelopment.model.Category;
import com.darvybm.project.apidevelopment.service.impl.CategoryServiceImpl;
import com.darvybm.project.apidevelopment.service.impl.ProductServiceImpl;
import com.darvybm.project.apidevelopment.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataFakerInitializer implements CommandLineRunner {

    private final Faker faker = new Faker();
    private final CategoryServiceImpl categoryService;
    private final ProductServiceImpl productService;
    private final UserServiceImpl userService;

    @Value("${createFakeData}")
    private boolean createFakeData;

    @Value("${fakeData.count}")
    private int fakeDataCount;

    @Override
    @Transactional
    public void run(String... args) {
        if (createFakeData) {
            createFakeCategories();
            createFakeProducts();
            createFakeUsers();
        }
    }

    private void createFakeCategories() {
        IntStream.range(0, fakeDataCount).forEach(i -> {
            CategoryRequest categoryRequest = new CategoryRequest();
            categoryRequest.setName(faker.commerce().department());
            categoryRequest.setDescription(faker.lorem().sentence());

            categoryService.save(categoryRequest);
        });
        System.out.println("Fake categories created.");
    }

    private void createFakeProducts() {
        List<Category> categories = categoryService.getAll();

        if (categories.isEmpty()) {
            System.out.println("No categories available to assign to products.");
            return;
        }

        IntStream.range(0, fakeDataCount).forEach(i -> {
            ProductRequest productRequest = new ProductRequest();
            productRequest.setName(faker.commerce().productName());
            productRequest.setDescription(faker.lorem().sentence());
            productRequest.setPrice(Double.parseDouble(faker.commerce().price()));
            productRequest.setStockQuantity(faker.number().numberBetween(1, 100));
            productRequest.setSupplier(faker.company().name());

            // Seleccionar un `categoryId` aleatorio de la lista de categorías
            UUID randomCategoryId = categories.get(faker.number().numberBetween(0, categories.size())).getId();
            productRequest.setCategoryId(randomCategoryId.toString());

            productService.save(productRequest);
        });

        System.out.println("Fake products created.");
    }

    private void createFakeUsers() {
        IntStream.range(0, fakeDataCount).forEach(i -> {
            UserRequest userRequest = new UserRequest();
            userRequest.setUsername(faker.name().username());
            userRequest.setPassword(faker.internet().password(8, 16));
            userRequest.setEmail(faker.internet().emailAddress());

            userService.save(userRequest);
        });
        System.out.println("Fake users created.");
    }
}
