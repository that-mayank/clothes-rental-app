package com.nineleaps.leaps.dto.product;

import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.categories.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private @NotNull String name;
    private @NotNull String imageURL;
    private @NotNull double price;
    private @NotNull String description;
    private @NotNull List<Long> subcategoryIds;

    public ProductDto(Product product) {
        this.setId(product.getId());
        this.setName(product.getName());
        this.setImageURL(product.getImageURL());
        this.setDescription(product.getDescription());
        this.setPrice(product.getPrice());
//        this.setSubcategoryIds(product.getSubCategories().stream().map(SubCategory::getId).collect(Collectors.toList()));
        List<Long> subCategoryIds = new ArrayList<>();
        for (SubCategory subCategory : product.getSubCategories()) {
            subCategoryIds.add(subCategory.getId());
        }
        this.setSubcategoryIds(subCategoryIds);
    }
}
