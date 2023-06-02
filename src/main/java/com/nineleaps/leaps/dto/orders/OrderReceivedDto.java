package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.orders.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.nineleaps.leaps.LeapsApplication.NGROK;

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
    private String borrowerName;
    private String borrowerEmail;
    private String borrowerPhoneNumber;

    public OrderReceivedDto(OrderItem orderItem) {
        this.name = orderItem.getProduct().getName();
        this.quantity = orderItem.getQuantity();
        this.rentalStartDate = orderItem.getRentalStartDate();
        this.rentalEndDate = orderItem.getRentalEndDate();
        this.rentalCost = Math.round(orderItem.getPrice() * orderItem.getQuantity() * (ChronoUnit.DAYS.between(orderItem.getRentalStartDate(), orderItem.getRentalEndDate())));
        int i = orderItem.getImageUrl().indexOf("/api");
        this.imageUrl = NGROK + orderItem.getImageUrl().substring(i);
        this.productId = orderItem.getProduct().getId();
        this.borrowerId = orderItem.getOrder().getUser().getId();
        this.borrowerName = orderItem.getOrder().getUser().getFirstName() + " " + orderItem.getOrder().getUser().getLastName();
        this.borrowerEmail = orderItem.getOrder().getUser().getEmail();
        this.borrowerPhoneNumber = orderItem.getOrder().getUser().getPhoneNumber();
    }
}