package com.nineleaps.leaps.dto.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SubCategoryDto {
    private Long id;
    @NotNull
    private String subcategoryName;
    @NotNull
    private String imageURL;
    @NotNull
    private String description;
    @NotNull
    private Long categoryId;

    public SubCategoryDto(String subcategoryName, String imageURL, String description, Long categoryId) {
        this.subcategoryName = subcategoryName;
        this.imageURL = imageURL;
        this.description = description;
        this.categoryId = categoryId;
    }
}
