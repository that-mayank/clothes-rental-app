package com.nineleaps.leaps.model.product;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;


class ProductTest {

    @Test
    void testAuditColumnsAndRelationships() {
        // Mock a user for audit columns
        User user = new User();
        user.setId(1L);
        LocalDateTime craeateAt = LocalDateTime.now();
        user.setCreatedAt(craeateAt);
        user.setCreatedBy(1L);


        // Create a sample product
        ProductDto productDto = new ProductDto();
        // Set productDto properties

        List<SubCategory> subCategories = new ArrayList<>();
        // Add subCategories

        List<Category> categories = new ArrayList<>();
        // Add categories

        Product product = new Product(productDto, subCategories, categories, user);
        product.setId(1L);
        product.setCreatedAt(craeateAt);
        product.setUpdatedBy(1L);
        product.setCreatedBy(1L);
        product.setUpdatedAt(craeateAt);

        assertEquals(craeateAt,product.getCreatedAt());
        assertEquals(1L, product.getCreatedBy());
        assertEquals(craeateAt, product.getUpdatedAt());
        assertEquals(1L, product.getUpdatedBy());

        // Test relationships
        List<Wishlist> wishlists = new ArrayList<>();
        product.setWishlists(wishlists);
        assertEquals(wishlists, product.getWishlists());

        List<Cart> carts = new ArrayList<>();
        product.setCarts(carts);
        assertEquals(carts, product.getCarts());
    }

    // Additional test cases for other properties and methods

}



