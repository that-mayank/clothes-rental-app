package com.nineleaps.leaps.dto.product;

import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static com.nineleaps.leaps.LeapsApplication.NGROK;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {


    private Long id;
    private String brand;
    private @NotNull String name;
    private @NotNull List<String> imageUrl;
    private @NotNull double price;
    private @NotNull String description;
    private @NotNull int totalQuantity;
    private int availableQuantities;
    private int disabledQuantities;
    private int rentedQuantities;
    private @NotNull String size;
    private String color;
    private String material;
    private @NotNull List<Long> subcategoryIds;
    private @NotNull List<Long> categoryIds;
    private boolean disabled = false;

    public ProductDto(Product product) {
        this.setId(product.getId());
        this.setName(product.getName());
        this.setImageUrl(product.getImageURL().stream().map(productUrl -> NGROK + productUrl.getUrl()).collect(Collectors.toList()));
        this.setDescription(product.getDescription());
        this.setPrice(product.getPrice());
        this.setTotalQuantity(product.getQuantity());
        this.setAvailableQuantities(product.getAvailableQuantities());
        this.setDisabledQuantities(product.getDisabledQuantities());
        this.setRentedQuantities(product.getRentedQuantities());
        this.setSize(product.getSize());
        this.setSubcategoryIds(product.getSubCategories().stream().map(SubCategory::getId).collect(Collectors.toList()));
        this.setCategoryIds(product.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
        this.setBrand(product.getBrand());
        this.setColor(product.getColor());
        this.setMaterial(product.getMaterial());
        this.setDisabled(product.isDisabled());
    }

}
