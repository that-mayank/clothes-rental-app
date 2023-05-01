package com.nineleaps.leaps.model.products;

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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_subcategory",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "subcategory_id"))
    @JsonIgnore
    List<SubCategory> subCategories = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JsonIgnore
    List<Category> categories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Wishlist> wishlists;

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Cart> carts;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @Column(name = "image_url")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductUrl> imageURL;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NotNull String brand;
    private @NotNull String name;
    private @NotNull double price;
    private @NotNull String description;
    private @NotNull String color;
    private @NotNull String material;
    private @NotNull int quantity;
    private @NotNull String size;
    @Column(name = "listing_start_date")
    private @NotNull LocalDateTime listingStartDate;
    @Column(name = "listing_end_date")
    private @NotNull LocalDateTime listingEndDate;
    @Column(name = "security_deposit")
    private @NotNull double securityDeposit;

    public Product(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user) {
        this.name = productDto.getName();
        this.price = productDto.getPrice();
        this.description = productDto.getDescription();
        this.quantity = productDto.getQuantity();
        this.size = productDto.getSize();
        this.brand = productDto.getBrand();
        this.color = productDto.getColor();
        this.material = productDto.getMaterial();
        this.subCategories = subCategories;
        this.categories = categories;
        this.user = user;
        this.listingStartDate = productDto.getListingStartDate();
        this.listingEndDate = productDto.getListingEndDate();
        this.securityDeposit = productDto.getSecurityDeposit();
    }
}
