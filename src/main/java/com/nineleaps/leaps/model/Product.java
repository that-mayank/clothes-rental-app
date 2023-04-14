package com.nineleaps.leaps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NotNull String name;
    private @NotNull String imageURL;
    private @NotNull double price;
    private @NotNull String description;
    private @NotNull int quantity;
    private @NotNull String size;

    public Product(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories) {
        this.name = productDto.getName();
        this.imageURL = productDto.getImageURL();
        this.price = productDto.getPrice();
        this.description = productDto.getDescription();
        this.quantity = productDto.getQuantity();
        this.size = productDto.getSize();
        this.subCategories = subCategories;
        this.categories = categories;
    }

}
