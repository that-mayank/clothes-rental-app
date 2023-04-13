package com.nineleaps.leaps.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartDto {
    private List<CartItemDto> cartItems;
    private double totalCost;

    public CartDto(List<CartItemDto> cartItems, double totalCost) {
        this.cartItems = cartItems;
        this.totalCost = totalCost;
    }
}
