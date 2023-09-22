package com.nineleaps.leaps.model;

import com.nineleaps.leaps.model.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents a Wishlist entity that stores information about user's favorite products.
 * Users can add products to their wishlist for later consideration or purchase.
 */
@Entity
@Table(name = "wishlist")
@Getter
@Setter
@NoArgsConstructor
public class Wishlist {
    /**
     * The unique identifier for a Wishlist item (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who owns this Wishlist item.
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    /**
     * The product added to the user's wishlist.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * The date and time when the Wishlist item was created.
     */
    @Column(name = "create_date")
    private Date createDate;

    /**
     * Creates a new Wishlist item with the specified product and user.
     *
     * @param product The product added to the wishlist.
     * @param user    The user who owns the wishlist.
     */
    public Wishlist(Product product, User user) {
        this.user = user;
        this.product = product;
        this.createDate = new Date();
    }
}
