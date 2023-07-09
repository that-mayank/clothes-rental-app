package com.nineleaps.leaps.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private @NotBlank String categoryName;
    private @NotBlank String description;
    private @NotBlank String imageUrl;
}
