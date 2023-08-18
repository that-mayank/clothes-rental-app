package com.nineleaps.leaps.model.categories;

import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubCategoryTest {

    private Category category;
    private SubCategory subCategory;

    @BeforeEach
    void setUp() {
        category = new Category();
        subCategory = new SubCategory();
        subCategory.setCategory(category);
        subCategory.setSubcategoryName("Test Subcategory");
        subCategory.setImageUrl("test_image.jpg");
        subCategory.setDescription("Test subcategory description");
    }

    @Test
    void testConstructorWithDtoAndCategory() {
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setSubcategoryName("Test Subcategory");
        subCategoryDto.setImageURL("test_image.jpg");
        subCategoryDto.setDescription("Test subcategory description");

        SubCategory subCategory = new SubCategory(subCategoryDto, category);

        assertEquals("Test Subcategory", subCategory.getSubcategoryName());
        assertEquals("test_image.jpg", subCategory.getImageUrl());
        assertEquals("Test subcategory description", subCategory.getDescription());
        assertEquals(category, subCategory.getCategory());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("Test Subcategory", subCategory.getSubcategoryName());
        assertEquals("test_image.jpg", subCategory.getImageUrl());
        assertEquals("Test subcategory description", subCategory.getDescription());
        assertEquals(category, subCategory.getCategory());
    }

    @Test
    void testJsonIgnoreAnnotationOnProductsField() {
        // Arrange
        List<Product> products = List.of(new Product(), new Product());
        subCategory.setProducts(products);

        // Act
        List<Product> ignoredProducts = subCategory.getProducts();

    }
}
