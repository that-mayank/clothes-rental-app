package com.nineleaps.leaps.model.categories;


import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("SubCategory Tests")
class SubCategoryTest {

    @Test
    @DisplayName("Test setting and getting subCategoryUpdatedAt")
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
    @DisplayName("Test setting and getting products property")
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
    @DisplayName("Test SubCategoryDto constructor")
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
}

