package com.nineleaps.leaps.dto.checkout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutItemDtoTest {

    private CheckoutItemDto checkoutItemDto;

    @BeforeEach
    void setUp() {
        checkoutItemDto = new CheckoutItemDto();
    }

    @Test
    void getUserId() {
        // Prepare
        Long userId = 123L;
        checkoutItemDto.setUserId(userId);

        // Execute
        Long result = checkoutItemDto.getUserId();

        // Verify
        assertEquals(userId, result);
    }

    @Test
    void getProductId() {
        // Prepare
        Long productId = 456L;
        checkoutItemDto.setProductId(productId);

        // Execute
        Long result = checkoutItemDto.getProductId();

        // Verify
        assertEquals(productId, result);
    }

    @Test
    void getProductName() {
        // Prepare
        String productName = "Example Product";
        checkoutItemDto.setProductName(productName);

        // Execute
        String result = checkoutItemDto.getProductName();

        // Verify
        assertEquals(productName, result);
    }

    @Test
    void getQuantity() {
        // Prepare
        int quantity = 3;
        checkoutItemDto.setQuantity(quantity);

        // Execute
        int result = checkoutItemDto.getQuantity();

        // Verify
        assertEquals(quantity, result);
    }

    @Test
    void getPrice() {
        // Prepare
        double price = 9.99;
        checkoutItemDto.setPrice(price);

        // Execute
        double result = checkoutItemDto.getPrice();

        // Verify
        assertEquals(price, result);
    }

    @Test
    void setUserId() {
        // Prepare
        Long userId = 123L;

        // Execute
        checkoutItemDto.setUserId(userId);

        // Verify
        assertEquals(userId, checkoutItemDto.getUserId());
    }

    @Test
    void setProductId() {
        // Prepare
        Long productId = 456L;

        // Execute
        checkoutItemDto.setProductId(productId);

        // Verify
        assertEquals(productId, checkoutItemDto.getProductId());
    }

    @Test
    void setProductName() {
        // Prepare
        String productName = "Example Product";

        // Execute
        checkoutItemDto.setProductName(productName);

        // Verify
        assertEquals(productName, checkoutItemDto.getProductName());
    }

    @Test
    void setQuantity() {
        // Prepare
        int quantity = 3;

        // Execute
        checkoutItemDto.setQuantity(quantity);

        // Verify
        assertEquals(quantity, checkoutItemDto.getQuantity());
    }

    @Test
    void setPrice() {
        // Prepare
        double price = 9.99;

        // Execute
        checkoutItemDto.setPrice(price);

        // Verify
        assertEquals(price, checkoutItemDto.getPrice());
    }

    @Test
    void testCheckoutItemDtoConstructor() {
        // Create sample data
        Long userId = 1L;
        Long productId = 100L;
        String productName = "Example Product";
        int quantity = 2;
        double price = 19.99;

        // Create the CheckoutItemDto instance using the constructor
        CheckoutItemDto checkoutItemDto = new CheckoutItemDto(userId, productId, productName, quantity, price);

        // Verify the values using assertions
        assertThat(checkoutItemDto.getUserId()).isEqualTo(userId);
        assertThat(checkoutItemDto.getProductId()).isEqualTo(productId);
        assertThat(checkoutItemDto.getProductName()).isEqualTo(productName);
        assertThat(checkoutItemDto.getQuantity()).isEqualTo(quantity);
        assertThat(checkoutItemDto.getPrice()).isEqualTo(price);
    }

}
