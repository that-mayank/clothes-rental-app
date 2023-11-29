package com.nineleaps.leaps.model.categories;

import com.nineleaps.leaps.dto.category.CategoryDto;
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
@DisplayName("Category Tests")
class CategoryTest {

    @Test
    @DisplayName("Test setting and getting categoryUpdatedAt")
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
    @DisplayName("Test setting and getting products property")
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
    @DisplayName("Test CategoryDto constructor")
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

