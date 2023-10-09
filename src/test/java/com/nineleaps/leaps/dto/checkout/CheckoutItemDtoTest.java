package com.nineleaps.leaps.dto.checkout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("CheckoutItemDto Tests")
class CheckoutItemDtoTest {

    @Test
    @DisplayName("CheckoutItemDto Creation Test")
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

    @Test
    @DisplayName("CheckoutItemDto Setters Test")
     void testSetters() {
        // Arrange
        CheckoutItemDto checkoutItemDto = new CheckoutItemDto();

        // Act
        checkoutItemDto.setUserId(1L);
        checkoutItemDto.setProductId(101L);
        checkoutItemDto.setProductName("Sample Product");
        checkoutItemDto.setQuantity(5);
        checkoutItemDto.setPrice(20.0);

        // Assert
        assertEquals(1L, checkoutItemDto.getUserId().longValue());
        assertEquals(101L, checkoutItemDto.getProductId().longValue());
        assertEquals("Sample Product", checkoutItemDto.getProductName());
        assertEquals(5, checkoutItemDto.getQuantity());
        assertEquals(20.0, checkoutItemDto.getPrice(), 0.001); // Allow a small tolerance for double comparisons
    }
}
