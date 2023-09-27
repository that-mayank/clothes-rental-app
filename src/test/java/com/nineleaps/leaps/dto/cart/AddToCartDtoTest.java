package com.nineleaps.leaps.dto.cart;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AddToCartDtoTest {

    @Test
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
    void getId() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setId(1L);

        assertEquals(1L, addToCartDto.getId());
    }

    @Test
    void getProductId() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(100L);

        assertEquals(100L, addToCartDto.getProductId());
    }

    @Test
    void getQuantity() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setQuantity(5);

        assertEquals(5, addToCartDto.getQuantity());
    }

    @Test
    void getRentalStartDate() {
        AddToCartDto addToCartDto = new AddToCartDto();
        LocalDateTime rentalStartDate = LocalDateTime.now();
        addToCartDto.setRentalStartDate(rentalStartDate);

        assertEquals(rentalStartDate, addToCartDto.getRentalStartDate());
    }

    @Test
    void getRentalEndDate() {
        AddToCartDto addToCartDto = new AddToCartDto();
        LocalDateTime rentalEndDate = LocalDateTime.now();
        addToCartDto.setRentalEndDate(rentalEndDate);

        assertEquals(rentalEndDate, addToCartDto.getRentalEndDate());
    }

    @Test
    void setId() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setId(1L);

        assertEquals(1L, addToCartDto.getId());
    }

    @Test
    void setProductId() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(100L);

        assertEquals(100L, addToCartDto.getProductId());
    }

    @Test
    void setQuantity() {
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setQuantity(5);

        assertEquals(5, addToCartDto.getQuantity());
    }

    @Test
    void setRentalStartDate() {
        AddToCartDto addToCartDto = new AddToCartDto();
        LocalDateTime rentalStartDate = LocalDateTime.now();
        addToCartDto.setRentalStartDate(rentalStartDate);

        assertEquals(rentalStartDate, addToCartDto.getRentalStartDate());
    }

    @Test
    void setRentalEndDate() {
        AddToCartDto addToCartDto = new AddToCartDto();
        LocalDateTime rentalEndDate = LocalDateTime.now();
        addToCartDto.setRentalEndDate(rentalEndDate);

        assertEquals(rentalEndDate, addToCartDto.getRentalEndDate());
    }
}