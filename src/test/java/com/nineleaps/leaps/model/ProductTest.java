package com.nineleaps.leaps.model;



import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        // Initialize test data
        List<SubCategory> subCategories = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        User user = new User();

        ProductDto productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setPrice(9.99);
        productDto.setDescription("Test Description");
        productDto.setTotalQuantity(10);
        productDto.setSize("M");
        productDto.setBrand("Test Brand");
        productDto.setColor("Test Color");
        productDto.setMaterial("Test Material");

        product = new Product(productDto, subCategories, categories, user);
    }

    @Test
    void testProductCreation() {
        // Verify product fields
        assertEquals("Test Product", product.getName());
        assertEquals(9.99, product.getPrice());
        assertEquals("Test Description", product.getDescription());
        assertEquals(10, product.getQuantity());
        assertEquals("M", product.getSize());
        assertEquals("Test Brand", product.getBrand());
        assertEquals("Test Color", product.getColor());
        assertEquals("Test Material", product.getMaterial());

        // Verify associations
        assertEquals(0, product.getSubCategories().size());
        assertEquals(0, product.getCategories().size());
        assertNotNull(product.getUser());
    }
}
