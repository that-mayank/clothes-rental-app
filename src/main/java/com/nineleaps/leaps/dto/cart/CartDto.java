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
    private double shippingCost;
    private double tax;
    private double totalCost;
    private double finalPrice;
    private Long userId;

    public CartDto(List<CartItemDto> cartItems, double totalCost, double tax, double finalPrice, long userId) {
        this.cartItems = cartItems;
        this.totalCost = totalCost;
        this.tax = tax;
        this.shippingCost = 100;
        this.finalPrice = finalPrice + shippingCost;
        this.userId = userId;
    }
}
