package com.nineleaps.leaps.dto.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddToCartDtoTest {

    private AddToCartDto addToCartDto;

    @BeforeEach
    void setUp() {
        // Create an instance of AddToCartDto for testing
        addToCartDto = new AddToCartDto();
    }

    @Test
    void getId() {
        // Test the getter method for id
        addToCartDto.setId(1L);
        assertEquals(1L, addToCartDto.getId());
    }

    @Test
    void getProductId() {
        // Test the getter method for productId
        addToCartDto.setProductId(2L);
        assertEquals(2L, addToCartDto.getProductId());
    }

    @Test
    void getQuantity() {
        // Test the getter method for quantity
        addToCartDto.setQuantity(3);
        assertEquals(3, addToCartDto.getQuantity());
    }

    @Test
    void getRentalStartDate() {
        // Test the getter method for rentalStartDate
        LocalDateTime startDate = LocalDateTime.now();
        addToCartDto.setRentalStartDate(startDate);
        assertEquals(startDate, addToCartDto.getRentalStartDate());
    }

    @Test
    void getRentalEndDate() {
        // Test the getter method for rentalEndDate
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        addToCartDto.setRentalEndDate(endDate);
        assertEquals(endDate, addToCartDto.getRentalEndDate());
    }

    @Test
    void setId() {
        // Test the setter method for id
        addToCartDto.setId(4L);
        assertEquals(4L, addToCartDto.getId());
    }

    @Test
    void setProductId() {
        // Test the setter method for productId
        addToCartDto.setProductId(5L);
        assertEquals(5L, addToCartDto.getProductId());
    }

    @Test
    void setQuantity() {
        // Test the setter method for quantity
        addToCartDto.setQuantity(6);
        assertEquals(6, addToCartDto.getQuantity());
    }

    @Test
    void setRentalStartDate() {
        // Test the setter method for rentalStartDate
        LocalDateTime startDate = LocalDateTime.now();
        addToCartDto.setRentalStartDate(startDate);
        assertEquals(startDate, addToCartDto.getRentalStartDate());
    }

    @Test
    void setRentalEndDate() {
        // Test the setter method for rentalEndDate
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        addToCartDto.setRentalEndDate(endDate);
        assertEquals(endDate, addToCartDto.getRentalEndDate());
    }
}
