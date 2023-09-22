package com.nineleaps.leaps.model.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Represents a SubCategory entity that stores information about subcategories within a category.
 * Each subcategory can have multiple associated products.
 */
@Entity
@Table(name = "subcategories")
@Getter
@Setter
@NoArgsConstructor
public class SubCategory {
    /**
     * The Category to which this subcategory belongs (Many-to-One relationship).
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    /**
     * The unique identifier for a SubCategory (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the subcategory.
     */
    @Column(name = "subcategory_name")
    private String subcategoryName;

    /**
     * The URL to an image representing the subcategory.
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * A brief description of the subcategory.
     */
    private String description;

    /**
     * The list of products associated with this subcategory.
     */
    @ManyToMany(mappedBy = "subCategories")
    @JsonIgnore
    private List<Product> products;

    /**
     * Creates a new SubCategory with the information provided in the SubCategoryDto.
     *
     * @param subCategoryDto The DTO (Data Transfer Object) containing subcategory information.
     * @param category       The parent category to which this subcategory belongs.
     */
    public SubCategory(SubCategoryDto subCategoryDto, Category category) {
        this.subcategoryName = subCategoryDto.getSubcategoryName();
        this.imageUrl = subCategoryDto.getImageURL();
        this.description = subCategoryDto.getDescription();
        this.category = category;
    }
}
