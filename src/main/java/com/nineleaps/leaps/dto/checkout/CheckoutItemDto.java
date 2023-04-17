package com.nineleaps.leaps.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutItemDto {
    private Long userId;
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
}
