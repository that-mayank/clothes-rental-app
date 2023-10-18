package com.nineleaps.leaps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Represents a user's shopping cart, containing selected products and associated information.
 */
@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The date when the cart was created
    @Column(name = "create_date")
    private Date createDate;

    // The product added to the cart
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // The user who owns the cart
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    @JsonIgnore
    private User user;

    // The quantity of the product in the cart
    private int quantity;

    // The start date of the rental period (if applicable)
    private LocalDateTime rentalStartDate;

    // The end date of the rental period (if applicable)
    private LocalDateTime rentalEndDate;

    // The URL of the product's image
    private String imageUrl;

    /**
     * Creates a Cart object with the specified product, user, quantity, rental dates, and image URL.
     *
     * @param product         The product added to the cart.
     * @param user            The user who owns the cart.
     * @param quantity        The quantity of the product in the cart.
     * @param rentalStartDate The start date of the rental period (if applicable).
     * @param rentalEndDate   The end date of the rental period (if applicable).
     * @param imageUrl        The list of product URLs, from which the first URL is used as the image URL.
     */
    public Cart(Product product, User user, int quantity, LocalDateTime rentalStartDate, LocalDateTime rentalEndDate, List<ProductUrl> imageUrl) {
        // Set the create date to the current date and time
        this.createDate = new Date();

        // Assign other provided values
        this.product = product;
        this.user = user;
        this.quantity = quantity;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        // Use the first image URL from the list (if available) as the cart item's image URL
        this.imageUrl = imageUrl.stream()
                .findFirst()
                .map(ProductUrl::getUrl)
                .orElse(null);
    }
}
