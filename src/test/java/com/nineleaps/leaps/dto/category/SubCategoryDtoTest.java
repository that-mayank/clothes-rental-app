package com.nineleaps.leaps.dto.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import static org.junit.jupiter.api.Assertions.*;

class SubCategoryDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGetId() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setId(1L);

        // Act
        Long id = subCategoryDto.getId();

        // Assert
        assertEquals(1L, id);
    }

    @Test
    void testGetSubcategoryName() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Subcategory");

        // Act
        String subcategoryName = subCategoryDto.getSubcategoryName();

        // Assert
        assertEquals("Subcategory", subcategoryName);
    }

    @Test
    void testGetImageURL() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setImageURL("image-url");

        // Act
        String imageURL = subCategoryDto.getImageURL();

        // Assert
        assertEquals("image-url", imageURL);
    }

    @Test
    void testGetDescription() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setDescription("Subcategory description");

        // Act
        String description = subCategoryDto.getDescription();

        // Assert
        assertEquals("Subcategory description", description);
    }

    @Test
    void testGetCategoryId() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setCategoryId(1L);

        // Act
        Long categoryId = subCategoryDto.getCategoryId();

        // Assert
        assertEquals(1L, categoryId);
    }

    @Test
    void testSetId() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();

        // Act
        subCategoryDto.setId(1L);

        // Assert
        assertEquals(1L, subCategoryDto.getId());
    }

    @Test
    void testSetSubcategoryName() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();

        // Act
        subCategoryDto.setSubcategoryName("Subcategory");

        // Assert
        assertEquals("Subcategory", subCategoryDto.getSubcategoryName());
    }

    @Test
    void testSetImageURL() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();

        // Act
        subCategoryDto.setImageURL("image-url");

        // Assert
        assertEquals("image-url", subCategoryDto.getImageURL());
    }

    @Test
    void testSetDescription() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();

        // Act
        subCategoryDto.setDescription("Subcategory description");

        // Assert
        assertEquals("Subcategory description", subCategoryDto.getDescription());
    }

    @Test
    void testSetCategoryId() {
        // Arrange
        SubCategoryDto subCategoryDto = new SubCategoryDto();

        // Act
        subCategoryDto.setCategoryId(1L);

        // Assert
        assertEquals(1L, subCategoryDto.getCategoryId());
    }
}
