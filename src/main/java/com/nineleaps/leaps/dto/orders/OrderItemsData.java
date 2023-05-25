package com.nineleaps.leaps.dto.orders;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class OrderItemsData {
    List<OrderReceivedDto> orderItems;
    private int totalOrders;

    public void incrementTotalOrders(@NotNull int quantity) {
        totalOrders = totalOrders + quantity;
    }
}
