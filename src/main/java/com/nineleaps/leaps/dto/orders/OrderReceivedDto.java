package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.orders.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.nineleaps.leaps.LeapsApplication.NGROK;

@Getter
@Setter
@NoArgsConstructor
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

        if (this.rentalStartDate != null && this.rentalEndDate != null) {
            long rentalDays = ChronoUnit.DAYS.between(this.rentalStartDate, this.rentalEndDate);
            this.rentalCost = Math.round(orderItem.getPrice() * this.quantity * rentalDays);
        } else {
            this.rentalCost = 0.0;  // Or any default value you prefer
        }

        int i = orderItem.getImageUrl().indexOf("/api");
        this.imageUrl = NGROK + orderItem.getImageUrl().substring(i);
        this.productId = orderItem.getProduct().getId();
        this.borrowerId = orderItem.getOrder().getUser().getId();
        this.borrowerName = orderItem.getOrder().getUser().getFirstName() + " " + orderItem.getOrder().getUser().getLastName();
        this.borrowerEmail = orderItem.getOrder().getUser().getEmail();
        this.borrowerPhoneNumber = orderItem.getOrder().getUser().getPhoneNumber();
    }

}