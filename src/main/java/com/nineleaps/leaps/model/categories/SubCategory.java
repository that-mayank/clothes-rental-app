package com.nineleaps.leaps.model.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "subcategories")
@Getter
@Setter
@NoArgsConstructor
public class SubCategory {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "subcategory_name")
    private String subcategoryName;
    @Column(name = "image_url")
    private String imageUrl;
    private String description;
    @ManyToMany(mappedBy = "subCategories")
    @JsonIgnore
    private List<Product> products;

    public SubCategory(SubCategoryDto subCategoryDto, Category category) {
        this.subcategoryName = subCategoryDto.getSubcategoryName();
        this.imageUrl = subCategoryDto.getImageURL();
        this.description = subCategoryDto.getDescription();
        this.category = category;
    }
}
