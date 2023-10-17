package com.nineleaps.leaps.dto.product;

import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductDtoTest {

    private ProductDto productDto;
    private Product product;

    @BeforeEach
    void setUp() {
        // Create a Product object
        product = new Product();
        product.setId(1L);
        product.setName("Sample Product");
        product.setPrice(100.0);
        product.setDescription("Sample product description");
        product.setQuantity(10);
        product.setAvailableQuantities(5);
        product.setDisabledQuantities(2);
        product.setRentedQuantities(3);
        product.setSize("M");
        product.setBrand("Sample Brand");
        product.setColor("Red");
        product.setMaterial("Cotton");
        product.setDisabled(false);

        List<SubCategory> subCategories = new ArrayList<>();
        SubCategory subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategories.add(subCategory);
        product.setSubCategories(subCategories);

        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setId(1L);
        categories.add(category);
        product.setCategories(categories);

        // Create a ProductDto from the Product object
        productDto = new ProductDto(product);
    }

    @Test
    void getId() {
        assertEquals(1L, productDto.getId());
    }

    @Test
    void getBrand() {
        assertEquals("Sample Brand", productDto.getBrand());
    }

    @Test
    void getName() {
        assertEquals("Sample Product", productDto.getName());
    }

    @Test
    void getImageUrl() {
        productDto.setImageUrl(Collections.singletonList("/xyz.jpeg"));
        assertEquals(List.of("/xyz.jpeg"), productDto.getImageUrl());
    }

    @Test
    void getPrice() {
        assertEquals(100.0, productDto.getPrice(), 0.01);
    }

    @Test
    void getDescription() {
        assertEquals("Sample product description", productDto.getDescription());
    }

    @Test
    void getTotalQuantity() {
        assertEquals(10, productDto.getTotalQuantity());
    }

    @Test
    void getAvailableQuantities() {
        assertEquals(5, productDto.getAvailableQuantities());
    }

    @Test
    void getDisabledQuantities() {
        assertEquals(2, productDto.getDisabledQuantities());
    }

    @Test
    void getRentedQuantities() {
        assertEquals(3, productDto.getRentedQuantities());
    }

    @Test
    void getSize() {
        assertEquals("M", productDto.getSize());
    }

    @Test
    void getColor() {
        assertEquals("Red", productDto.getColor());
    }

    @Test
    void getMaterial() {
        assertEquals("Cotton", productDto.getMaterial());
    }

    @Test
    void getSubcategoryIds() {
        assertEquals(1L, productDto.getSubcategoryIds().get(0));
    }

    @Test
    void getCategoryIds() {
        assertEquals(1L, productDto.getCategoryIds().get(0));
    }

    @Test
    void isDisabled() {
        assertEquals(false, productDto.isDisabled());
    }

    @Test
    void setId() {
        productDto.setId(2L);
        assertEquals(2L, productDto.getId());
    }

    @Test
    void setBrand() {
        productDto.setBrand("New Brand");
        assertEquals("New Brand", productDto.getBrand());
    }

    @Test
    void setName() {
        productDto.setName("New Product");
        assertEquals("New Product", productDto.getName());
    }

    @Test
    void setPrice() {
        productDto.setPrice(200.0);
        assertEquals(200.0, productDto.getPrice(), 0.01);
    }

    @Test
    void setDescription() {
        productDto.setDescription("New product description");
        assertEquals("New product description", productDto.getDescription());
    }

    @Test
    void setTotalQuantity() {
        productDto.setTotalQuantity(20);
        assertEquals(20, productDto.getTotalQuantity());
    }

    @Test
    void setAvailableQuantities() {
        productDto.setAvailableQuantities(15);
        assertEquals(15, productDto.getAvailableQuantities());
    }

    @Test
    void setDisabledQuantities() {
        productDto.setDisabledQuantities(5);
        assertEquals(5, productDto.getDisabledQuantities());
    }

    @Test
    void setRentedQuantities() {
        productDto.setRentedQuantities(10);
        assertEquals(10, productDto.getRentedQuantities());
    }

    @Test
    void setSize() {
        productDto.setSize("S");
        assertEquals("S", productDto.getSize());
    }

    @Test
    void setColor() {
        productDto.setColor("Blue");
        assertEquals("Blue", productDto.getColor());
    }

    @Test
    void setMaterial() {
        productDto.setMaterial("Polyester");
        assertEquals("Polyester", productDto.getMaterial());
    }

    @Test
    void setSubcategoryIds() {
        List<Long> subcategoryIds = new ArrayList<>();
        subcategoryIds.add(2L);
        productDto.setSubcategoryIds(subcategoryIds);
        assertEquals(2L, productDto.getSubcategoryIds().get(0));
    }

    @Test
    void setCategoryIds() {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(2L);
        productDto.setCategoryIds(categoryIds);
        assertEquals(2L, productDto.getCategoryIds().get(0));
    }

    @Test
    void setDisabled() {
        productDto.setDisabled(true);
        assertEquals(true, productDto.isDisabled());
    }
}
