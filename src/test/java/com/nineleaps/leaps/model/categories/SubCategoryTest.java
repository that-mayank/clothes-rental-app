package com.nineleaps.leaps.model.categories;


import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubCategoryTest {

    @Test
    void testSubCategoryUpdatedAt() {
        // Arrange
        SubCategory subCategory = new SubCategory();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        subCategory.setSubCategoryUpdatedAt(updatedAt);

        // Assert
        assertEquals(updatedAt, subCategory.getSubCategoryUpdatedAt());
    }

    @Test
    void testProductsProperty() {
        // Arrange
        SubCategory subCategory = new SubCategory();
        List<Product> products = new ArrayList<>();

        // Act
        subCategory.setProducts(products);

        // Assert
        assertEquals(products, subCategory.getProducts());
    }

    @Test
    void testSubCategoryDtoConstructor() {
        // Arrange
        Category category = new Category();  // Create a category instance if needed
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Test Subcategory");
        subCategoryDto.setImageURL("http://example.com/image.jpg");
        subCategoryDto.setDescription("Test Description");

        // Act
        SubCategory subCategory = new SubCategory(subCategoryDto, category);

        // Assert
        assertEquals(subCategoryDto.getSubcategoryName(), subCategory.getSubcategoryName());
        assertEquals(subCategoryDto.getImageURL(), subCategory.getImageUrl());
        assertEquals(subCategoryDto.getDescription(), subCategory.getDescription());
    }

    // Add more test cases as needed

}
