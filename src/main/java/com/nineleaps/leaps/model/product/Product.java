package com.nineleaps.leaps.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Product entity that stores information about a product available in an e-commerce platform.
 * Products can be associated with categories, subcategories, users, and have various attributes.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedProductFilter", condition = "deleted = :isDeleted")
@FilterDef(name = "disabledProductFilter", parameters = @ParamDef(name = "isDisabled", type = "boolean"))
@Filter(name = "disabledProductFilter", condition = "disabled = :isDisabled")
public class Product {

    /**
     * The unique identifier for a Product (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The brand of the product.
     */
    private String brand;

    /**
     * The name of the product (not nullable).
     */
    private @NotNull String name;

    /**
     * The price of the product (not nullable).
     */
    private @NotNull double price;

    /**
     * The description of the product (not nullable).
     */
    private @NotNull String description;

    /**
     * The color of the product.
     */
    private String color;

    /**
     * The material of the product.
     */
    private String material;

    /**
     * The quantity of the product (not nullable).
     */
    private @NotNull int quantity;

    /**
     * The available quantities of the product.
     */
    @Column(name = "available_quantities")
    private int availableQuantities;

    /**
     * The disabled quantities of the product.
     */
    @Column(name = "disabled_quantities")
    private int disabledQuantities;

    /**
     * The rented quantities of the product.
     */
    @Column(name = "rented_quantities")
    private int rentedQuantities;

    /**
     * The size of the product (not nullable).
     */
    private @NotNull String size;

    /**
     * Indicates if the product is deleted or not (not nullable).
     */
    private @NotNull boolean deleted = Boolean.FALSE;

    /**
     * Indicates if the product is disabled or not (not nullable).
     */
    private @NotNull boolean disabled = Boolean.FALSE;

    /**
     * List of subcategories associated with the product (Many-to-Many relationship).
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_subcategory",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "subcategory_id"))
    @JsonIgnore
    List<SubCategory> subCategories = new ArrayList<>();

    /**
     * List of categories associated with the product (Many-to-Many relationship).
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JsonIgnore
    List<Category> categories = new ArrayList<>();

    /**
     * List of wishlists containing this product (One-to-Many relationship).
     */
    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Wishlist> wishlists;

    /**
     * List of carts containing this product (One-to-Many relationship).
     */
    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Cart> carts;

    /**
     * The user associated with this product (Many-to-One relationship).
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * List of image URLs associated with this product (One-to-Many relationship).
     */
    @JsonIgnore
    @Column(name = "image_url")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductUrl> imageURL = new ArrayList<>();

    /**
     * Constructor to create a Product based on a ProductDto, subcategories, categories, and user.
     * @param productDto The ProductDto containing product information.
     * @param subCategories The list of subcategories associated with the product.
     * @param categories The list of categories associated with the product.
     * @param user The user associated with the product.
     */
    public Product(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user) {
        this.name = productDto.getName();
        this.price = productDto.getPrice();
        this.description = productDto.getDescription();
        this.quantity = productDto.getTotalQuantity();
        this.availableQuantities = productDto.getTotalQuantity();
        this.disabledQuantities = 0;
        this.rentedQuantities = 0;
        this.size = productDto.getSize();
        this.brand = productDto.getBrand();
        this.color = productDto.getColor();
        this.material = productDto.getMaterial();
        this.subCategories = subCategories;
        this.categories = categories;
        this.user = user;
    }
}
