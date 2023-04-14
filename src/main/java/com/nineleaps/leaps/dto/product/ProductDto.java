package com.nineleaps.leaps.dto.product;

import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private @NotNull String name;
    private @NotNull String imageURL;
    private @NotNull double price;
    private @NotNull String description;
    private @NotNull int quantity;
    private @NotNull String size;
    private @NotNull List<Long> subcategoryIds;
    private @NotNull List<Long> categoryIds;

    public ProductDto(Product product) {
        this.setId(product.getId());
        this.setName(product.getName());
        this.setImageURL(product.getImageURL());
        this.setDescription(product.getDescription());
        this.setPrice(product.getPrice());
        this.setQuantity(product.getQuantity());
        this.setSize(product.getSize());

        this.setSubcategoryIds(product.getSubCategories().stream().map(SubCategory::getId).collect(Collectors.toList()));

        this.setCategoryIds(product.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
    }
}
