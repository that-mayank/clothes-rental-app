package com.nineleaps.leaps.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents a ProductUrl entity that stores URLs associated with a product.
 */
@Entity
@Table(name = "product_url")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductUrl {

    /**
     * The unique identifier for a ProductUrl (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The URL string.
     */
    private String url;

    /**
     * The product associated with this URL (Many-to-One relationship).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
