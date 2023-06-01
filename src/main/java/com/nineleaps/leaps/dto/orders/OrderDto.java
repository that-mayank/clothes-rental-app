package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private LocalDateTime createdDate;
    private double totalPrice;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.createdDate = order.getCreateDate();
        this.totalPrice = order.getTotalPrice();
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            OrderItemDto orderItemDto = new OrderItemDto(orderItem);
            orderItemDtos.add(orderItemDto);
        }
        this.orderItems = orderItemDtos;
    }
}
