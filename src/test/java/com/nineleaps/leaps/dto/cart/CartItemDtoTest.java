package com.nineleaps.leaps.dto.cart;

import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CartItemDtoTest {

    @Test
    void testToString() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setQuantity(5);
        cartItemDto.setProduct(new Product());
        cartItemDto.setRentalStartDate(LocalDateTime.now());
        cartItemDto.setRentalEndDate(LocalDateTime.now());
        cartItemDto.setImageUrl("image-url");

        String expectedToString = "CartItemDto{" +
                "id=1, " +
                "quantity=5, " +
                "product=" + cartItemDto.getProduct() +
                "}";
        assertEquals(expectedToString, cartItemDto.toString());
    }

    @Test
    void getId() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);

        assertEquals(1L, cartItemDto.getId());
    }

    @Test
    void getQuantity() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setQuantity(5);

        assertEquals(5, cartItemDto.getQuantity());
    }

    @Test
    void getProduct() {
        Product product = new Product();
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProduct(product);

        assertEquals(product, cartItemDto.getProduct());
    }

    @Test
    void getRentalStartDate() {
        LocalDateTime rentalStartDate = LocalDateTime.now();
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setRentalStartDate(rentalStartDate);

        assertEquals(rentalStartDate, cartItemDto.getRentalStartDate());
    }

    @Test
    void getRentalEndDate() {
        LocalDateTime rentalEndDate = LocalDateTime.now();
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setRentalEndDate(rentalEndDate);

        assertEquals(rentalEndDate, cartItemDto.getRentalEndDate());
    }

    @Test
    void getImageUrl() {
        String imageUrl = "image-url";
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setImageUrl(imageUrl);

        assertEquals(imageUrl, cartItemDto.getImageUrl());
    }

    @Test
    void setId() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);

        assertEquals(1L, cartItemDto.getId());
    }

    @Test
    void setQuantity() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setQuantity(5);

        assertEquals(5, cartItemDto.getQuantity());
    }

    @Test
    void setProduct() {
        Product product = new Product();
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProduct(product);

        assertEquals(product, cartItemDto.getProduct());
    }

    @Test
    void setRentalStartDate() {
        LocalDateTime rentalStartDate = LocalDateTime.now();
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setRentalStartDate(rentalStartDate);

        assertEquals(rentalStartDate, cartItemDto.getRentalStartDate());
    }

    @Test
    void setRentalEndDate() {
        LocalDateTime rentalEndDate = LocalDateTime.now();
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setRentalEndDate(rentalEndDate);

        assertEquals(rentalEndDate, cartItemDto.getRentalEndDate());
    }

    @Test
    void setImageUrl() {
        String imageUrl = "image-url";
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setImageUrl(imageUrl);

        assertEquals(imageUrl, cartItemDto.getImageUrl());
    }
}
