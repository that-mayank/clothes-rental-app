package com.nineleaps.leaps.dto.cart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@Tag("unit_tests")
@DisplayName("AddToCartDto Tests")
class AddToCartDtoTest {

    @Test
    @DisplayName("Test toString")
    void testToString() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setId(1L);
        addToCartDto.setProductId(100L);
        addToCartDto.setQuantity(5);

        String expectedToString = "AddToCartDto{" +
                "id=1, " +
                "productId=100, " +
                "quantity=5" +
                "}";
        assertEquals(expectedToString, addToCartDto.toString());
    }

    @Test
    @DisplayName("Test getId")
    void getId() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setId(1L);

        assertEquals(1L, addToCartDto.getId());
    }

    @Test
    @DisplayName("Test getProductId")
    void getProductId() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(100L);

        assertEquals(100L, addToCartDto.getProductId());
    }

    @Test
    @DisplayName("Test getQuantity")
    void getQuantity() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setQuantity(5);

        assertEquals(5, addToCartDto.getQuantity());
    }

    @Test
    @DisplayName("Test getRentalStartDate")
    void getRentalStartDate() {
        AddToCartDto addToCartDto = new AddToCartDto();
        LocalDateTime rentalStartDate = LocalDateTime.now();
        addToCartDto.setRentalStartDate(rentalStartDate);

        assertEquals(rentalStartDate, addToCartDto.getRentalStartDate());
    }

    @Test
    @DisplayName("Test getRentalEndDate")
    void getRentalEndDate() {
        AddToCartDto addToCartDto = new AddToCartDto();
        LocalDateTime rentalEndDate = LocalDateTime.now();
        addToCartDto.setRentalEndDate(rentalEndDate);

        assertEquals(rentalEndDate, addToCartDto.getRentalEndDate());
    }

    @Test
    @DisplayName("Test setId")
    void setId() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setId(1L);

        assertEquals(1L, addToCartDto.getId());
    }

    @Test
    @DisplayName("Test setProductId")
    void setProductId() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(100L);

        assertEquals(100L, addToCartDto.getProductId());
    }

    @Test
    @DisplayName("Test setQuantity")
    void setQuantity() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setQuantity(5);

        assertEquals(5, addToCartDto.getQuantity());
    }

    @Test
    @DisplayName("Test setRentalStartDate")
    void setRentalStartDate() {
        AddToCartDto addToCartDto = new AddToCartDto();
        LocalDateTime rentalStartDate = LocalDateTime.now();
        addToCartDto.setRentalStartDate(rentalStartDate);

        assertEquals(rentalStartDate, addToCartDto.getRentalStartDate());
    }

    @Test
    @DisplayName("Test setRentalEndDate")
    void setRentalEndDate() {
        AddToCartDto addToCartDto = new AddToCartDto();
        LocalDateTime rentalEndDate = LocalDateTime.now();
        addToCartDto.setRentalEndDate(rentalEndDate);

        assertEquals(rentalEndDate, addToCartDto.getRentalEndDate());
    }
}