package com.nineleaps.leaps.dto.category;

import com.nineleaps.leaps.model.categories.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SubCategoryDto {
    private Long id;
    private @NotNull String subcategoryName;
    private @NotNull String imageURL;
    private @NotNull String description;
    private @NotNull Long categoryId;

    public SubCategoryDto(SubCategory subCategory) {
        this.setId(subCategory.getId());
        this.setSubcategoryName(subCategory.getSubcategoryName());
        this.setImageURL(subCategory.getImageURL());
        this.setDescription(subCategory.getDescription());
        this.setCategoryId(subCategory.getCategory().getId());
    }
}

