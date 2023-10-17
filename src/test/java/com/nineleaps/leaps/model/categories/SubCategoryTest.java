package com.nineleaps.leaps.model.categories;

import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubCategoryTest {

    private SubCategory subCategory;

    @BeforeEach
    void setUp() {
        subCategory = new SubCategory();
    }

    @Test
    void getCategory() {
        Category category = new Category();
        subCategory.setCategory(category);
        assertEquals(category, subCategory.getCategory());
    }

    @Test
    void getId() {
        subCategory.setId(1L);
        assertEquals(1L, subCategory.getId());
    }

    @Test
    void getSubcategoryName() {
        subCategory.setSubcategoryName("Test SubCategory");
        assertEquals("Test SubCategory", subCategory.getSubcategoryName());
    }

    @Test
    void getImageUrl() {
        subCategory.setImageUrl("test.jpg");
        assertEquals("test.jpg", subCategory.getImageUrl());
    }

    @Test
    void getDescription() {
        subCategory.setDescription("Test Description");
        assertEquals("Test Description", subCategory.getDescription());
    }

    @Test
    void getProducts() {
        // Assuming you have a method to set the products
        // For this example, we are using an empty list
        subCategory.setProducts(Collections.emptyList());
        assertEquals(Collections.emptyList(), subCategory.getProducts());
    }

    @Test
    void setCategory() {
        Category category = new Category();
        subCategory.setCategory(category);
        assertEquals(category, subCategory.getCategory());
    }

    @Test
    void setId() {
        subCategory.setId(2L);
        assertEquals(2L, subCategory.getId());
    }

    @Test
    void setSubcategoryName() {
        subCategory.setSubcategoryName("New SubCategory");
        assertEquals("New SubCategory", subCategory.getSubcategoryName());
    }

    @Test
    void setImageUrl() {
        subCategory.setImageUrl("new.jpg");
        assertEquals("new.jpg", subCategory.getImageUrl());
    }

    @Test
    void setDescription() {
        subCategory.setDescription("New Description");
        assertEquals("New Description", subCategory.getDescription());
    }

    @Test
    void setProducts() {
        // Assuming you have a method to set the products
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        products.add(new Product());
        subCategory.setProducts(products);
        assertEquals(products, subCategory.getProducts());
    }

    @Test
    void constructorWithSubCategoryDto() {
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Dto SubCategory");
        subCategoryDto.setImageURL("dto.jpg");
        subCategoryDto.setDescription("Dto Description");

        Category category = new Category();
        subCategory = new SubCategory(subCategoryDto, category);

        assertEquals("Dto SubCategory", subCategory.getSubcategoryName());
        assertEquals("dto.jpg", subCategory.getImageUrl());
        assertEquals("Dto Description", subCategory.getDescription());
        assertEquals(category, subCategory.getCategory());
    }
}
