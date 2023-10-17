package com.nineleaps.leaps.dto.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubCategoryDtoTest {

    private SubCategoryDto subCategoryDto;

    @BeforeEach
    void setUp() {
        subCategoryDto = new SubCategoryDto();
        subCategoryDto.setId(1L);
        subCategoryDto.setSubcategoryName("Men");
        subCategoryDto.setImageURL("/images/Men.jpg");
        subCategoryDto.setDescription("Men subcategory description");
        subCategoryDto.setCategoryId(10L);
    }

    @Test
    void getId() {
        assertEquals(1L, subCategoryDto.getId());
    }

    @Test
    void getSubcategoryName() {
        assertEquals("Men", subCategoryDto.getSubcategoryName());
    }

    @Test
    void getImageURL() {
        assertEquals("/images/Men.jpg", subCategoryDto.getImageURL());
    }

    @Test
    void getDescription() {
        assertEquals("Men subcategory description", subCategoryDto.getDescription());
    }

    @Test
    void getCategoryId() {
        assertEquals(10L, subCategoryDto.getCategoryId());
    }

    @Test
    void setId() {
        subCategoryDto.setId(2L);
        assertEquals(2L, subCategoryDto.getId());
    }

    @Test
    void setSubcategoryName() {
        subCategoryDto.setSubcategoryName("Clothing");
        assertEquals("Clothing", subCategoryDto.getSubcategoryName());
    }

    @Test
    void setImageURL() {
        subCategoryDto.setImageURL("/images/clothing.jpg");
        assertEquals("/images/clothing.jpg", subCategoryDto.getImageURL());
    }

    @Test
    void setDescription() {
        subCategoryDto.setDescription("Clothing subcategory description");
        assertEquals("Clothing subcategory description", subCategoryDto.getDescription());
    }

    @Test
    void setCategoryId() {
        subCategoryDto.setCategoryId(20L);
        assertEquals(20L, subCategoryDto.getCategoryId());
    }
}
