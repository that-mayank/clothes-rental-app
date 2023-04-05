package com.nineleaps.leaps.dto.category;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    private @NotBlank String categoryName;
    private @NotBlank String description;
    private @NotBlank String imageUrl;
}
