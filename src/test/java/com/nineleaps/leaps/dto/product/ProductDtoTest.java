package com.nineleaps.leaps.dto.product;


import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ProductDtoTest {

    @Test
    void testConstructor() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setDescription("Description");
        product.setPrice(100.0);
        product.setQuantity(10);
        product.setAvailableQuantities(5);
        product.setDisabledQuantities(2);
        product.setRentedQuantities(3);
        product.setSize("Large");
        product.setBrand("Brand");
        product.setColor("Blue");
        product.setMaterial("Cotton");
        product.setDisabled(false);



        ProductUrl productUrl = new ProductUrl();
        productUrl.setProduct(product);
        productUrl.setUrl("/profile.jpg");
        productUrl.setId(1L);

        ProductUrl productUrl2 = new ProductUrl();
        productUrl2.setProduct(product);
        productUrl2.setUrl("/profile2.jpg");
        productUrl2.setId(1L);
        List<ProductUrl> productUrls = new ArrayList<>();
        productUrls.add(productUrl);
        productUrls.add(productUrl2);
        product.setImageURL(productUrls);



        SubCategory subCategory1 = new SubCategory();
        subCategory1.setId(1L);
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setId(2L);

        List<SubCategory> subCategories = new ArrayList<>();
        subCategories.add(subCategory1);
        subCategories.add(subCategory2);
        product.setSubCategories(subCategories);

        Category category1 = new Category();
        category1.setId(10L);
        Category category2 = new Category();
        category2.setId(11L);

        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);
        product.setCategories(categories);

        // Act
        ProductDto productDto = new ProductDto(product);

        // Assert
        assertEquals(product.getId(), productDto.getId());
        assertEquals(product.getName(), productDto.getName());
        assertEquals(NGROK + product.getImageURL().get(0).getUrl(), productDto.getImageUrl().get(0));
        assertEquals(product.getDescription(), productDto.getDescription());
        assertEquals(product.getPrice(), productDto.getPrice());
        assertEquals(product.getQuantity(), productDto.getTotalQuantity());
        assertEquals(product.getAvailableQuantities(), productDto.getAvailableQuantities());
        assertEquals(product.getDisabledQuantities(), productDto.getDisabledQuantities());
        assertEquals(product.getRentedQuantities(), productDto.getRentedQuantities());
        assertEquals(product.getSize(), productDto.getSize());
        assertEquals(product.getSubCategories().get(0).getId(), productDto.getSubcategoryIds().get(0));
        assertEquals(product.getCategories().get(0).getId(), productDto.getCategoryIds().get(0));
        assertEquals(product.getBrand(), productDto.getBrand());
        assertEquals(product.getColor(), productDto.getColor());
        assertEquals(product.getMaterial(), productDto.getMaterial());
        assertEquals(product.isDisabled(), productDto.isDisabled());
    }

    @Test
    void testSetters() {
        // Arrange
        ProductDto productDto = new ProductDto();
        List<String> imageUrl = new ArrayList<>();
        imageUrl.add("https://example.com/image.jpg");

        // Act
        productDto.setImageUrl(imageUrl);

        // Assert
        assertEquals(imageUrl, productDto.getImageUrl());
    }
}
