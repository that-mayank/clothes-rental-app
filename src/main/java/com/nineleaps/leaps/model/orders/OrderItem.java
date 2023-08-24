package com.nineleaps.leaps.model.orders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.model.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NotNull String name;
    @Column(name = "quantity")
    private @NotNull int quantity;
    @Column(name = "price")
    private @NotNull double price;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
    private @NotNull LocalDateTime rentalStartDate;
    private @NotNull LocalDateTime rentalEndDate;
    private String imageUrl;
    private String status;
    private @NotNull double securityDeposit;
    @Column(name = "owner_id")
    private Long ownerId;
    //TODO -- confirm status
    //TODO -- shipped status
}
