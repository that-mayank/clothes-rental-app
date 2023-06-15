package com.nineleaps.leaps.dto.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateProductQuantityDto {
    private @NotNull Long productId;
    private @NotNull int quantity;
}
