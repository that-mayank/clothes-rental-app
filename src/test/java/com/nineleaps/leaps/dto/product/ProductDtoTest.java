package com.nineleaps.leaps.dto.product;

import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.ProductUrl;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.*;

class ProductDtoTest {

    private Product product;
    private ProductDto productDto;
    @BeforeEach
    void setUp() {
        product = new Product();
        // Set up the properties of the Product for testing

        // Set other properties of the Product
        product.setId(1L);  // Set the product ID
        product.setBrand("Brand");  // Set the brand
        product.setName("Product Name");  // Set the product name

        // Create a list of ProductUrl for the Product
        List<ProductUrl> imageURL = new ArrayList<>();
        imageURL.add(new ProductUrl(1L,"/api/v1/file/view/image1.jpg",product));
        imageURL.add(new ProductUrl(2L,"/api/v1/file/view/image2.jpg",product));
        product.setImageURL(imageURL);

        product.setPrice(99.99);  // Set the price
        product.setDescription("Product description");  // Set the description
        product.setQuantity(100);  // Set the total quantity
        product.setAvailableQuantities(80);  // Set the available quantities
        product.setDisabledQuantities(10);  // Set the disabled quantities
        product.setRentedQuantities(10);  // Set the rented quantities
        product.setSize("XL");  // Set the size
        List<Category> categories = new ArrayList<>();
        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(1L);
        categoryDto1.setCategoryName("Category 1");
        // Set other properties of categoryDto1
        Category category1 = new Category(categoryDto1);
        categories.add(category1);

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setCategoryName("Category 2");
        // Set other properties of categoryDto2
        Category category2 = new Category(categoryDto2);
        categories.add(category2);
        // Create a list of SubCategory for the Product
        List<SubCategory> subCategories = new ArrayList<>();
        SubCategoryDto subCategoryDto1 = new SubCategoryDto();
        subCategoryDto1.setId(1L);
        subCategoryDto1.setSubcategoryName("Subcategory 1");
        subCategoryDto1.setImageURL("/api/v1/file/view/image3/jpg");
        subCategoryDto1.setCategoryId(1L);
        // Set other properties of subCategoryDto1
        SubCategory subCategory1 = new SubCategory(subCategoryDto1, category1);
        subCategories.add(subCategory1);

        SubCategoryDto subCategoryDto2 = new SubCategoryDto();
        subCategoryDto2.setId(2L);
        subCategoryDto2.setSubcategoryName("Subcategory 2");
        subCategoryDto2.setImageURL("/api/v1/file/view/image3/jpg");
        subCategoryDto2.setCategoryId(2L);
        // Set other properties of subCategoryDto2
        SubCategory subCategory2 = new SubCategory(subCategoryDto2, category2);
        subCategories.add(subCategory2);

        product.setSubCategories(subCategories);

        // Create a list of Category for the Product


        product.setCategories(categories);

        product.setDisabled(true);  // Set the disabled flag

        productDto = new ProductDto(product);
    }
    @Test
    void getImageUrl() {
        List<String> expectedImageUrls = List.of(NGROK+"/api/v1/file/view/image1.jpg", NGROK+"/api/v1/file/view/image2.jpg");
        assertEquals(expectedImageUrls, productDto.getImageUrl());
    }

    @Test
    void getPrice() {
        assertEquals(99.99, productDto.getPrice());
    }

    @Test
    void getDescription() {
        assertEquals("Product description", productDto.getDescription());
    }

    @Test
    void getTotalQuantity() {
        assertEquals(100, productDto.getTotalQuantity());
    }

    @Test
    void getAvailableQuantities() {
        assertEquals(80, productDto.getAvailableQuantities());
    }

    @Test
    void getDisabledQuantities() {
        assertEquals(10, productDto.getDisabledQuantities());
    }

    @Test
    void getRentedQuantities() {
        assertEquals(10, productDto.getRentedQuantities());
    }

    @Test
    void getSize() {
        assertEquals("XL", productDto.getSize());
    }

    @Test
    void getColor() {
        assertNull(productDto.getColor());
    }

    @Test
    void getMaterial() {
        assertNull(productDto.getMaterial());
    }

//    @Test
//    void getSubcategoryIds() {
//        List<Long> expectedSubcategoryIds = List.of(null,null);
//        assertEquals(expectedSubcategoryIds, productDto.getSubcategoryIds());
//    }

    @Test
    void getCategoryIds() {
        List<Long> expectedCategoryIds = List.of(1L, 2L);
        assertEquals(expectedCategoryIds, productDto.getCategoryIds());
    }

    @Test
    void isDisabled() {
        assertTrue(productDto.isDisabled());
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
        productDto.setName("New Product Name");
        assertEquals("New Product Name", productDto.getName());
    }

    @Test
    void setImageUrl() {
        List<String> newImageUrls = List.of("new_image1.jpg", "new_image2.jpg");
        productDto.setImageUrl(newImageUrls);
        assertEquals(newImageUrls, productDto.getImageUrl());
    }

    @Test
    void setPrice() {
        productDto.setPrice(199.99);
        assertEquals(199.99, productDto.getPrice());
    }

    @Test
    void setDescription() {
        productDto.setDescription("New Product Description");
        assertEquals("New Product Description", productDto.getDescription());
    }

    @Test
    void setTotalQuantity() {
        productDto.setTotalQuantity(200);
        assertEquals(200, productDto.getTotalQuantity());
    }

    @Test
    void setAvailableQuantities() {
        productDto.setAvailableQuantities(180);
        assertEquals(180, productDto.getAvailableQuantities());
    }

    @Test
    void setDisabledQuantities() {
        productDto.setDisabledQuantities(20);
        assertEquals(20, productDto.getDisabledQuantities());
    }

    @Test
    void setRentedQuantities() {
        productDto.setRentedQuantities(15);
        assertEquals(15, productDto.getRentedQuantities());
    }

    @Test
    void setSize() {
        productDto.setSize("M");
        assertEquals("M", productDto.getSize());
    }

    @Test
    void setColor() {
        productDto.setColor("Red");
        assertEquals("Red", productDto.getColor());
    }

    @Test
    void setMaterial() {
        productDto.setMaterial("Cotton");
        assertEquals("Cotton", productDto.getMaterial());
    }

    @Test
    void setSubcategoryIds() {
        List<Long> newSubcategoryIds = List.of(3L, 4L);
        productDto.setSubcategoryIds(newSubcategoryIds);
        assertEquals(newSubcategoryIds, productDto.getSubcategoryIds());
    }

    @Test
    void setCategoryIds() {
        List<Long> newCategoryIds = List.of(3L, 4L);
        productDto.setCategoryIds(newCategoryIds);
        assertEquals(newCategoryIds, productDto.getCategoryIds());
    }

    @Test
    void setDisabled() {
        productDto.setDisabled(true);
        assertTrue(productDto.isDisabled());
    }
}
