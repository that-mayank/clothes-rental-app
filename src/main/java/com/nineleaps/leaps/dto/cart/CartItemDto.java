package com.nineleaps.leaps.dto.cart;

import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.products.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CartItemDto {
    private Long id;
    private @NotNull int quantity;
    private @NotNull Product product;
    private @NotNull LocalDateTime rentalStartDate;
    private @NotNull LocalDateTime rentalEndDate;

    public CartItemDto(Cart cart) {
        this.setId(cart.getId());
        this.setQuantity(cart.getQuantity());
        this.setProduct(cart.getProduct());
        this.setRentalStartDate(cart.getRentalStartDate());
        this.setRentalEndDate(cart.getRentalEndDate());
    }

    @Override
    public String toString() {
        return "CartItemDto{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", product=" + product +
                '}';
    }
}
