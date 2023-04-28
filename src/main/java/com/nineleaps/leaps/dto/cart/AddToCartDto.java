package com.nineleaps.leaps.dto.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class AddToCartDto {
    private Long id;
    private @NotNull Long productId;
    private @NotNull int quantity;
    private @NotNull LocalDateTime rentalStartDate;
    private @NotNull LocalDateTime rentalEndDate;

    @Override
    public String toString() {
        return "AddToCartDto{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
