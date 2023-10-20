package com.nineleaps.leaps.dto.cart;

import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartItemDtoTest {

    private CartItemDto cartItemDto;

    @BeforeEach
    void setUp() {
        // Create a mock Cart object for testing
        Cart cart = mock(Cart.class);
        Product product = new Product();
        product.setId(1L);
        when(cart.getId()).thenReturn(101L);
        when(cart.getQuantity()).thenReturn(3);
        when(cart.getProduct()).thenReturn(product);
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusDays(5);
        when(cart.getRentalStartDate()).thenReturn(startDateTime);
        when(cart.getRentalEndDate()).thenReturn(endDateTime);
        when(cart.getImageUrl()).thenReturn("/api/images/product.jpg");

        cartItemDto = new CartItemDto(cart);
    }

    @Test
    void getId() {
        assertEquals(101L, cartItemDto.getId());
    }

    @Test
    void getQuantity() {
        assertEquals(3, cartItemDto.getQuantity());
    }

    @Test
    void getProduct() {
        Product product = cartItemDto.getProduct();
        assertEquals(1L, product.getId());
    }

    @Test
    void getRentalStartDate() {
        LocalDateTime rentalStartDate = cartItemDto.getRentalStartDate();
        assertEquals(LocalDateTime.now().toLocalDate(), rentalStartDate.toLocalDate());
    }

    @Test
    void getRentalEndDate() {
        LocalDateTime rentalEndDate = cartItemDto.getRentalEndDate();
        assertEquals(LocalDateTime.now().plusDays(5).toLocalDate(), rentalEndDate.toLocalDate());
    }

    @Test
    void getImageUrl() {
        String imageUrl = cartItemDto.getImageUrl();
        int i = imageUrl.indexOf("/api");
        assertEquals(NGROK + "/api/images/product.jpg", imageUrl);
    }

    @Test
    void setId() {
        cartItemDto.setId(102L);
        assertEquals(102L, cartItemDto.getId());
    }

    @Test
    void setQuantity() {
        cartItemDto.setQuantity(5);
        assertEquals(5, cartItemDto.getQuantity());
    }

    @Test
    void setProduct() {
        Product newProduct = new Product();
        newProduct.setId(2L);
        cartItemDto.setProduct(newProduct);
        assertEquals(2L, cartItemDto.getProduct().getId());
    }

    @Test
    void setRentalStartDate() {
        LocalDateTime newStartDateTime = LocalDateTime.now().plusDays(1);
        cartItemDto.setRentalStartDate(newStartDateTime);
        assertEquals(newStartDateTime, cartItemDto.getRentalStartDate());
    }

    @Test
    void setRentalEndDate() {
        LocalDateTime newEndDateTime = LocalDateTime.now().plusDays(6);
        cartItemDto.setRentalEndDate(newEndDateTime);
        assertEquals(newEndDateTime, cartItemDto.getRentalEndDate());
    }

    @Test
    void setImageUrl() {
        cartItemDto.setImageUrl(NGROK + "/api/images/new_product.jpg");
        assertEquals(NGROK + "/api/images/new_product.jpg", cartItemDto.getImageUrl());
    }
}
