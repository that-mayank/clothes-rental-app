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

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "category_name")
    private @NotBlank String categoryName;
    private @NotBlank String description;
    @Column(name = "image_url")
    private @NotBlank String imageUrl;
    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Product> products;

    public Category(CategoryDto categoryDto) {
        this.id = categoryDto.getId();
        this.categoryName = categoryDto.getCategoryName();
        this.description = categoryDto.getDescription();
        this.imageUrl = categoryDto.getImageUrl();
    }
}
