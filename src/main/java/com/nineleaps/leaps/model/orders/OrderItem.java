package com.nineleaps.leaps.model.orders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.model.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "quantity")
    private @NotNull int quantity;
    @Column(name = "price")
    private @NotNull double price;
    @Column(name = "created_date")
    private Date createdDate;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    public OrderItem(@NotNull int quantity,@NotNull double price, Order order,@NotNull Product product) {
        this.quantity = quantity;
        this.price = price;
        this.order = order;
        this.product = product;
        this.createdDate = new Date();
    }
}
