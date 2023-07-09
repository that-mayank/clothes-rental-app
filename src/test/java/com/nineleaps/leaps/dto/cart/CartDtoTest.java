package com.nineleaps.leaps.dto.cart;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartDtoTest {

    @Test
    void getCartItems() {
        List<CartItemDto> cartItems = new ArrayList<>();
        cartItems.add(new CartItemDto());
        cartItems.add(new CartItemDto());

        CartDto cartDto = new CartDto();
        cartDto.setCartItems(cartItems);

        assertEquals(cartItems, cartDto.getCartItems());
    }

    @Test
    void getShippingCost() {
        CartDto cartDto = new CartDto();
        cartDto.setShippingCost(100.0);

        assertEquals(100.0, cartDto.getShippingCost());
    }

    @Test
    void getTax() {
        CartDto cartDto = new CartDto();
        cartDto.setTax(10.0);

        assertEquals(10.0, cartDto.getTax());
    }

    @Test
    void getTotalCost() {
        CartDto cartDto = new CartDto();
        cartDto.setTotalCost(500.0);

        assertEquals(500.0, cartDto.getTotalCost());
    }

    @Test
    void getFinalPrice() {
        CartDto cartDto = new CartDto();
        cartDto.setFinalPrice(600.0);

        assertEquals(600.0, cartDto.getFinalPrice());
    }

    @Test
    void setCartItems() {
        List<CartItemDto> cartItems = new ArrayList<>();
        cartItems.add(new CartItemDto());
        cartItems.add(new CartItemDto());

        CartDto cartDto = new CartDto();
        cartDto.setCartItems(cartItems);

        assertEquals(cartItems, cartDto.getCartItems());
    }

    @Test
    void setShippingCost() {
        CartDto cartDto = new CartDto();
        cartDto.setShippingCost(100.0);

        assertEquals(100.0, cartDto.getShippingCost());
    }

    @Test
    void setTax() {
        CartDto cartDto = new CartDto();
        cartDto.setTax(10.0);

        assertEquals(10.0, cartDto.getTax());
    }

    @Test
    void setTotalCost() {
        CartDto cartDto = new CartDto();
        cartDto.setTotalCost(500.0);

        assertEquals(500.0, cartDto.getTotalCost());
    }

    @Test
    void setFinalPrice() {
        CartDto cartDto = new CartDto();
        cartDto.setFinalPrice(600.0);

        assertEquals(600.0, cartDto.getFinalPrice());
    }
}
