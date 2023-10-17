package com.nineleaps.leaps.dto.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryDtoTest {

    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setCategoryName("Men");
        categoryDto.setDescription("Men category description");
        categoryDto.setImageUrl("/images/Men.jpg");
    }

    @Test
    void getId() {
        assertEquals(1L, categoryDto.getId());
    }

    @Test
    void getCategoryName() {
        assertEquals("Men", categoryDto.getCategoryName());
    }

    @Test
    void getDescription() {
        assertEquals("Men category description", categoryDto.getDescription());
    }

    @Test
    void getImageUrl() {
        assertEquals("/images/Men.jpg", categoryDto.getImageUrl());
    }

    @Test
    void setId() {
        categoryDto.setId(2L);
        assertEquals(2L, categoryDto.getId());
    }

    @Test
    void setCategoryName() {
        categoryDto.setCategoryName("Clothing");
        assertEquals("Clothing", categoryDto.getCategoryName());
    }

    @Test
    void setDescription() {
        categoryDto.setDescription("Clothing category description");
        assertEquals("Clothing category description", categoryDto.getDescription());
    }

    @Test
    void setImageUrl() {
        categoryDto.setImageUrl("/images/clothing.jpg");
        assertEquals("/images/clothing.jpg", categoryDto.getImageUrl());
    }
}
