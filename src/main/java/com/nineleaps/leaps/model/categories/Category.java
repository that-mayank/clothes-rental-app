package com.nineleaps.leaps.model.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.category.CategoryDto;
import com.nineleaps.leaps.model.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Represents a Category entity that stores information about product categories.
 * Each category can have multiple associated products.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {
    /**
     * The unique identifier for a Category (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the category.
     */
    @Column(name = "category_name")
    private @NotBlank String categoryName;

    /**
     * A brief description of the category.
     */
    private @NotBlank String description;

    /**
     * The URL to an image representing the category.
     */
    @Column(name = "image_url")
    private @NotBlank String imageUrl;

    /**
     * The list of products associated with this category.
     */
    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Product> products;

    /**
     * Creates a new Category with the information provided in the CategoryDto.
     *
     * @param categoryDto The DTO (Data Transfer Object) containing category information.
     */
    public Category(CategoryDto categoryDto) {
        this.id = categoryDto.getId();
        this.categoryName = categoryDto.getCategoryName();
        this.description = categoryDto.getDescription();
        this.imageUrl = categoryDto.getImageUrl();
    }
}
