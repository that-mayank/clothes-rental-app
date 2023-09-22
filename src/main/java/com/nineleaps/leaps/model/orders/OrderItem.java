package com.nineleaps.leaps.model.orders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.model.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Represents an OrderItem entity that stores information about individual items in a customer's order.
 * Each order item is associated with a product.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {
    /**
     * The unique identifier for an OrderItem (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product associated with this order item.
     */
    private @NotNull String name;

    /**
     * The quantity of the product in this order item.
     */
    @Column(name = "quantity")
    private @NotNull int quantity;

    /**
     * The price of a single unit of the product.
     */
    @Column(name = "price")
    private @NotNull double price;

    /**
     * The date and time when this order item was created.
     */
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    /**
     * The order to which this order item belongs (Many-to-One relationship).
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    /**
     * The product associated with this order item (One-to-One relationship).
     */
    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    /**
     * The start date and time for renting the product.
     */
    private @NotNull LocalDateTime rentalStartDate;

    /**
     * The end date and time for renting the product.
     */
    private @NotNull LocalDateTime rentalEndDate;

    /**
     * The URL of the product's image.
     */
    private String imageUrl;

    /**
     * The status of the order item.
     */
    private String status;

    /**
     * The security deposit amount for the product.
     */
    private @NotNull double securityDeposit;

    /**
     * The ID of the owner associated with the product.
     */
    @Column(name = "owner_id")
    private Long ownerId;
}
