package com.nineleaps.leaps.model.categories;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryTest {

    @Test
    void testCategoryUpdatedAt() {
        // Arrange
        Category category = new Category();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        category.setCategoryUpdatedAt(updatedAt);

        // Assert
        assertEquals(updatedAt, category.getCategoryUpdatedAt());
    }

    @Test
    void testProductsProperty() {
        // Arrange
        Category category = new Category();
        List<Product> products = new ArrayList<>();

        // Act
        category.setProducts(products);

        // Assert
        assertEquals(products, category.getProducts());
    }

    @Test
    void testCategoryDtoConstructor() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setCategoryName("Test Category");
        categoryDto.setDescription("Test Description");
        categoryDto.setImageUrl("http://example.com/image.jpg");

        // Act
        Category category = new Category(categoryDto);

        // Assert
        assertEquals(categoryDto.getId(), category.getId());
        assertEquals(categoryDto.getCategoryName(), category.getCategoryName());
        assertEquals(categoryDto.getDescription(), category.getDescription());
        assertEquals(categoryDto.getImageUrl(), category.getImageUrl());
    }
}
