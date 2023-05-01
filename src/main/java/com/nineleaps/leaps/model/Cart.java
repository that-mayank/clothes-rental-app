package com.nineleaps.leaps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.model.products.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "create_date")
    private Date createDate;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    @JsonIgnore
    private User user;
    private int quantity;
    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalEndDate;

    public Cart(Product product, User user, int quantity, LocalDateTime rentalStartDate, LocalDateTime rentalEndDate) {
        this.createDate = new Date();
        this.product = product;
        this.user = user;
        this.quantity = quantity;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
    }
}
