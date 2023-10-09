package com.nineleaps.leaps.dto.category;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import static org.junit.Assert.*;
@DisplayName("SubCategoryDto Tests")
@Tag("unit_tests")
public class SubCategoryDtoTest {

    @Test
    @DisplayName("Test Id")
    public void testId() {
        long expectedId = 123L; // Set the expected ID

        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setId(expectedId);

        long actualId = subCategoryDto.getId();

        assertEquals(expectedId, actualId);
    }

    @Test
    @DisplayName("Test Setters and Getters")
    public void testSettersAndGetters() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        Long id = 1L;
        String subcategoryName = "Test Subcategory";
        String imageURL = "https://example.com/image.jpg";
        String description = "Test description";
        Long categoryId = 100L;

        // Act
        subCategoryDto.setId(id);
        subCategoryDto.setSubcategoryName(subcategoryName);
        subCategoryDto.setImageURL(imageURL);
        subCategoryDto.setDescription(description);
        subCategoryDto.setCategoryId(categoryId);

        // Assert
        assertEquals(id, subCategoryDto.getId());
        assertEquals(subcategoryName, subCategoryDto.getSubcategoryName());
        assertEquals(imageURL, subCategoryDto.getImageURL());
        assertEquals(description, subCategoryDto.getDescription());
        assertEquals(categoryId, subCategoryDto.getCategoryId());

        // Test null values
        subCategoryDto.setId(null);
        subCategoryDto.setSubcategoryName(null);
        subCategoryDto.setImageURL(null);
        subCategoryDto.setDescription(null);
        subCategoryDto.setCategoryId(null);

        assertNull(subCategoryDto.getId());
        assertNull(subCategoryDto.getSubcategoryName());
        assertNull(subCategoryDto.getImageURL());
        assertNull(subCategoryDto.getDescription());
        assertNull(subCategoryDto.getCategoryId());
    }

}
