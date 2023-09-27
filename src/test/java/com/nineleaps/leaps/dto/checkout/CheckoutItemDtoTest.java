package com.nineleaps.leaps.dto.checkout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckoutItemDtoTest {

    @Test
    void checkoutItemDtoCreation() {
        // Sample data
        Long userId = 1L;
        Long productId = 101L;
        String productName = "Sample Product";
        int quantity = 2;
        double price = 20.0;

        // Create a CheckoutItemDto
        CheckoutItemDto checkoutItemDto = new CheckoutItemDto(userId, productId, productName, quantity, price);

        // Verify the properties of the CheckoutItemDto
        assertEquals(userId, checkoutItemDto.getUserId());
        assertEquals(productId, checkoutItemDto.getProductId());
        assertEquals(productName, checkoutItemDto.getProductName());
        assertEquals(quantity, checkoutItemDto.getQuantity());
        assertEquals(price, checkoutItemDto.getPrice());
    }
}
