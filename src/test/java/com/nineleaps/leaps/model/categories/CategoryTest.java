package com.nineleaps.leaps.model.categories;


import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.Product;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

 class CategoryTest {

    @Test
     void testCategoryGettersAndSetters() {
        // Create a sample Category instance
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Electronics");
        category.setDescription("Electronics category");
        category.setImageUrl("https://example.com/electronics.jpg");

        // Create a sample list of products
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("TV");
        products.add(product1);
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Laptop");
        products.add(product2);
        category.setProducts(products);

        // Verify the getters
        assertEquals(1L, category.getId());
        assertEquals("Electronics", category.getCategoryName());
        assertEquals("Electronics category", category.getDescription());
        assertEquals("https://example.com/electronics.jpg", category.getImageUrl());
        assertEquals(products, category.getProducts());
    }

    @Test
     void testCategoryConstructor() {
        // Create a CategoryDto instance
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setCategoryName("Electronics");
        categoryDto.setDescription("Electronics category");
        categoryDto.setImageUrl("https://example.com/electronics.jpg");

        // Create a Category instance using the constructor
        Category category = new Category(categoryDto);

        // Verify the values
        assertEquals(categoryDto.getId(), category.getId());
        assertEquals(categoryDto.getCategoryName(), category.getCategoryName());
        assertEquals(categoryDto.getDescription(), category.getDescription());
        assertEquals(categoryDto.getImageUrl(), category.getImageUrl());

    }
}