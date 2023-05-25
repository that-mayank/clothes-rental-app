package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.orders.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class OrderReceivedDto {
    private String name;
    private int quantity;
    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalEndDate;
    private double rentalCost;
    private String imageUrl;
    private Long productId;
    private Long borrowerId;

    public OrderReceivedDto(OrderItem orderItem) {
        this.name = orderItem.getProduct().getName();
        this.quantity = orderItem.getQuantity();
        this.rentalStartDate = orderItem.getRentalStartDate();
        this.rentalEndDate = orderItem.getRentalEndDate();
        this.rentalCost = Math.round(orderItem.getPrice() * orderItem.getQuantity() * (ChronoUnit.DAYS.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate())));
        this.imageUrl = orderItem.getImageUrl();
        this.productId = orderItem.getProduct().getId();
        this.borrowerId = orderItem.getOrder().getUser().getId();
    }
}