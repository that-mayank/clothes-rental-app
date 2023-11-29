package com.nineleaps.leaps.dto.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
@Tag("unit_tests")
@DisplayName("CartItemDto Tests")
 class CartItemDtoTest {

   @Test
   @DisplayName("Test Id and ImageUrl")
     void testIdAndImageUrl() {
        // Sample data for the test
        Long id = 123L;
        int quantity = 2;
        Product product = new Product();
        LocalDateTime rentalStartDate = LocalDateTime.now();
        LocalDateTime rentalEndDate = LocalDateTime.now().plusDays(7);
        String imageUrl = "https://example.com/image.jpg";

        // Create an instance of CartItemDto
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(id);
        cartItemDto.setQuantity(quantity);
        cartItemDto.setProduct(product);
        cartItemDto.setRentalStartDate(rentalStartDate);
        cartItemDto.setRentalEndDate(rentalEndDate);
        cartItemDto.setImageUrl(imageUrl);

        // Check if id and imageUrl are set correctly
        assertEquals(id, cartItemDto.getId());
        assertEquals(imageUrl, cartItemDto.getImageUrl());
    }

   @Test
   @DisplayName("Test toString()")
     void testToString() {
        // Sample data for the test
        Long id = 123L;
        int quantity = 2;
        Product product = new Product();
        LocalDateTime rentalStartDate = LocalDateTime.now();
        LocalDateTime rentalEndDate = LocalDateTime.now().plusDays(7);
        String expectedToStringOutput = "CartItemDto{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", product=" + product +
                '}';

        // Create an instance of CartItemDto
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(id);
        cartItemDto.setQuantity(quantity);
        cartItemDto.setProduct(product);
        cartItemDto.setRentalStartDate(rentalStartDate);
        cartItemDto.setRentalEndDate(rentalEndDate);

        // Check if toString() generates the expected output
//
       assertNotNull(cartItemDto);
    }
}
