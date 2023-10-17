package com.nineleaps.leaps.dto.cart;

import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartDtoTest {

    private CartDto cartDto;

    @BeforeEach
    void setUp() {
        // Create an instance of CartDto for testing
        List<CartItemDto> cartItems = new ArrayList<>();
        cartItems.add(new CartItemDto());
        cartItems.add(new CartItemDto());

        cartDto = new CartDto(cartItems, 100.0, 10.0, 120.0, 1L);
    }

    @Test
    void getCartItems() {
        List<CartItemDto> items = cartDto.getCartItems();
        assertEquals(2, items.size());
    }

    @Test
    void getShippingCost() {
        double shippingCost = cartDto.getShippingCost();
        assertEquals(100.0, shippingCost);
    }

    @Test
    void getTax() {
        double tax = cartDto.getTax();
        assertEquals(10.0, tax);
    }

    @Test
    void getTotalCost() {
        double totalCost = cartDto.getTotalCost();
        assertEquals(100.0, totalCost);
    }

    @Test
    void getFinalPrice() {
        double finalPrice = cartDto.getFinalPrice();
        assertEquals(220.0, finalPrice);
    }

    @Test
    void getUserId() {
        Long userId = cartDto.getUserId();
        assertEquals(1L, userId);
    }

    @Test
    void setCartItems() {
        List<CartItemDto> newCartItems = new ArrayList<>();
        newCartItems.add(new CartItemDto());
        cartDto.setCartItems(newCartItems);
        assertEquals(newCartItems, cartDto.getCartItems());
    }

    @Test
    void setShippingCost() {
        cartDto.setShippingCost(50.0);
        assertEquals(50.0, cartDto.getShippingCost());
    }

    @Test
    void setTax() {
        cartDto.setTax(15.0);
        assertEquals(15.0, cartDto.getTax());
    }

    @Test
    void setTotalCost() {
        cartDto.setTotalCost(150.0);
        assertEquals(150.0, cartDto.getTotalCost());
    }

    @Test
    void setFinalPrice() {
        cartDto.setFinalPrice(270.0);
        assertEquals(270.0, cartDto.getFinalPrice());
    }

    @Test
    void setUserId() {
        cartDto.setUserId(2L);
        assertEquals(2L, cartDto.getUserId());
    }
}
