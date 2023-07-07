package com.nineleaps.leaps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.product.ProductDto;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private @NotNull String name;
    private @NotNull double price;
    private @NotNull String description;
    private String color;
    private String material;
    private @NotNull int quantity;
    @Column(name = "available_quantities")
    private int availableQuantities;
    @Column(name = "disabled_quantities")
    private int disabledQuantities;
    @Column(name = "rented_quantities")
    private int rentedQuantities;
    private @NotNull String size;
    private @NotNull boolean deleted = Boolean.FALSE;
    private @NotNull boolean disabled = Boolean.FALSE;

    @JsonIgnore
    @Column(name = "image_url")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductUrl> imageURL = new ArrayList<>();

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
