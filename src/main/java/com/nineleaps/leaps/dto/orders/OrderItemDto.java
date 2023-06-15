package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.orders.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.nineleaps.leaps.LeapsApplication.NGROK;

@Getter
@Setter
public class OrderItemDto {
    private Long id;
    private Long productId;
    private String name;
    private int quantity;
    private double pricePerDay;
    private double totalPrice;
    private LocalDateTime createdDate;
    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalEndDate;
    private String imageUrl;
    private String status;

    public OrderItemDto(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.productId = orderItem.getProduct().getId();
        this.name = orderItem.getProduct().getName();
        this.quantity = orderItem.getQuantity();
        this.pricePerDay = orderItem.getPrice();
        this.createdDate = orderItem.getCreatedDate();
        this.rentalStartDate = orderItem.getRentalStartDate();
        this.rentalEndDate = orderItem.getRentalEndDate();
        this.imageUrl = NGROK + orderItem.getImageUrl();
        this.status = orderItem.getStatus();
        this.totalPrice = orderItem.getPrice() * orderItem.getQuantity();
    }
}
