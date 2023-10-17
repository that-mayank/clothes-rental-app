package com.nineleaps.leaps.model.categories;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    void getId() {
        category.setId(1L);
        assertEquals(1L, category.getId());
    }

    @Test
    void getCategoryName() {
        category.setCategoryName("Test Category");
        assertEquals("Test Category", category.getCategoryName());
    }

    @Test
    void getDescription() {
        category.setDescription("Test Description");
        assertEquals("Test Description", category.getDescription());
    }

    @Test
    void getImageUrl() {
        category.setImageUrl("test.jpg");
        assertEquals("test.jpg", category.getImageUrl());
    }

    @Test
    void getProducts() {
        category.setProducts(Collections.emptyList());
        assertEquals(Collections.emptyList(), category.getProducts());
    }

    @Test
    void setId() {
        category.setId(2L);
        assertEquals(2L, category.getId());
    }

    @Test
    void setCategoryName() {
        category.setCategoryName("New Category");
        assertEquals("New Category", category.getCategoryName());
    }

    @Test
    void setDescription() {
        category.setDescription("New Description");
        assertEquals("New Description", category.getDescription());
    }

    @Test
    void setImageUrl() {
        category.setImageUrl("new.jpg");
        assertEquals("new.jpg", category.getImageUrl());
    }

    @Test
    void setProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        products.add(new Product());
        category.setProducts(products);
        assertEquals(products, category.getProducts());
    }

    @Test
    void constructorWithCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(3L);
        categoryDto.setCategoryName("Dto Category");
        categoryDto.setDescription("Dto Description");
        categoryDto.setImageUrl("dto.jpg");

        category = new Category(categoryDto);

        assertEquals(3L, category.getId());
        assertEquals("Dto Category", category.getCategoryName());
        assertEquals("Dto Description", category.getDescription());
        assertEquals("dto.jpg", category.getImageUrl());
    }
}
