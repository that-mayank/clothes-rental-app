package com.nineleaps.leaps.dto.category;

import org.junit.Test;

import static org.junit.Assert.*;

public class SubCategoryDtoTest {

    @Test
    public void testIdGetter() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        Long id = 1L;

        // Act
        subCategoryDto.setId(id);

        // Assert
        assertEquals(id, subCategoryDto.getId());
    }

    @Test
    public void testSettersAndGetters() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        Long id = 1L;
        String subcategoryName = "Test Subcategory";
        String imageURL = "http://example.com/image.jpg";
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
