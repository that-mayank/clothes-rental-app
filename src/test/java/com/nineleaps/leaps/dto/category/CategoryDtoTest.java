package com.nineleaps.leaps.dto.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCategoryDto() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto(1L, "Books", "Category description", "image-url");

        // Act
        var violations = validator.validate(categoryDto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankCategoryName() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto(1L, "", "Category description", "image-url");

        // Act
        var violations = validator.validate(categoryDto);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("categoryName", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testBlankDescription() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto(1L, "Books", "", "image-url");

        // Act
        var violations = validator.validate(categoryDto);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testBlankImageUrl() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto(1L, "Books", "Category description", "");

        // Act
        var violations = validator.validate(categoryDto);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("imageUrl", violations.iterator().next().getPropertyPath().toString());
    }
}
