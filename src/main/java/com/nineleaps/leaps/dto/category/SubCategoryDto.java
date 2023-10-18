package com.nineleaps.leaps.dto.category;


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
}

