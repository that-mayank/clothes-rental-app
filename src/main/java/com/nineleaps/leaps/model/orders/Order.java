package com.nineleaps.leaps.model.orders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Order entity that stores information about customer orders.
 * Each order can have multiple associated order items.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    /**
     * The unique identifier for an Order (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The date and time when the order was created.
     */
    @Column(name = "create_date")
    private LocalDateTime createDate;

    /**
     * The total price of the order.
     */
    @Column(name = "total_price")
    private double totalPrice;

    /**
     * The session ID associated with the order.
     */
    @Column(name = "session_id")
    private String sessionId;

    /**
     * The list of order items associated with this order (One-to-Many relationship).
     */
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * The user who placed the order (Many-to-One relationship).
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
