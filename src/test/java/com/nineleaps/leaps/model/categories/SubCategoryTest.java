package com.nineleaps.leaps.model.categories;


import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

//    @Test
//     void testSetAuditColumnsCreate() {
//        // Create a User object with mock data
//        User user = mock(User.class);
//        LocalDateTime createdAt = LocalDateTime.now().minusDays(1); // Example createdAt date
//        long createdBy = 123L; // Example createdBy value
//
//        // Mocking behavior of user object
//        when(user.getCreatedAt()).thenReturn(createdAt);
//        when(user.getCreatedBy()).thenReturn(createdBy);
//
//        // Create a Category instance
//        Category category = new Category();
//
//        // Call the setAuditColumnsCreate method with the mocked user object
//        category.setAuditColumnsCreate(user);
//
//        // Validate the Category object's fields are set correctly
//        assertEquals(createdAt, category.getCategoryCreatedAt());
//        assertEquals(Long.valueOf(createdBy), category.getCategoryCreatedBy());
//    }
//
//    @Test
//     void testSetAuditColumnsUpdate() {
//        // Example user ID
//        Long userId = 456L;
//
//        // Create a Category instance
//        Category category = new Category();
//
//        // Call the setAuditColumnsUpdate method with the example user ID
//        category.setAuditColumnsUpdate(userId);
//
//        // Validate the Category object's fields are set correctly
//        assertEquals(userId, category.getCategoryUpdatedBy());
//        // Ensure categoryUpdatedAt is not null (may differ slightly from current time due to execution time)
//        assertEquals(true, category.getCategoryUpdatedAt() != null);
//    }

    @Test
     void testSetAuditColumnsCreate() {
        // Create a User object with mock data
        User user = mock(User.class);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1); // Example createdAt date
        long createdBy = 123L; // Example createdBy value

        // Mocking behavior of user object
        when(user.getCreatedAt()).thenReturn(createdAt);
        when(user.getCreatedBy()).thenReturn(createdBy);

        // Create a Category instance
        Category category = new Category();

        // Call the setAuditColumnsCreate method with the mocked user object
        category.setAuditColumnsCreate(user);

        // Validate the Category object's fields are set correctly
        assertEquals(createdAt, category.getCategoryCreatedAt());
        assertEquals(Long.valueOf(createdBy), category.getCategoryCreatedBy());
    }

    @Test
     void testSetAuditColumnsUpdate() {
        // Example user ID
        Long userId = 456L;

        // Create a Category instance
        Category category = new Category();

        // Call the setAuditColumnsUpdate method with the example user ID
        category.setAuditColumnsUpdate(userId);

        // Validate the Category object's fields are set correctly
        assertEquals(userId, category.getCategoryUpdatedBy());
        // Ensure categoryUpdatedAt is not null (may differ slightly from current time due to execution time)
        assertEquals(true, category.getCategoryUpdatedAt() != null);
    }
}

