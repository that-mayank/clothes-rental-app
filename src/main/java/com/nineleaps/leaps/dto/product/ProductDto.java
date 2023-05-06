package com.nineleaps.leaps.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.model.products.Product;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {

//    //after adding @Component the ngrokUrl is not accessible from application.properties
    @JsonIgnore
    @Value("${ngrok_url}")
    private String ngrokUrl;

    private Long id;
    private @NotNull String brand;
    private @NotNull String name;
    private @NotNull List<String> imageUrl;
    private @NotNull double price;
    private @NotNull String description;
    private @NotNull int quantity;
    private @NotNull String size;
    private @NotNull String color;
    private @NotNull String material;
    private @NotNull List<Long> subcategoryIds;
    private @NotNull List<Long> categoryIds;
    private @NotNull LocalDateTime listingStartDate;
    private @NotNull LocalDateTime listingEndDate;
    private @NotNull double securityDeposit;

    public ProductDto(Product product) {
        this.setId(product.getId());
        this.setName(product.getName());
        this.setImageUrl(product.getImageURL().stream().map(productUrl -> getNgrokUrl() + productUrl.getUrl()).collect(Collectors.toList()));
        this.setDescription(product.getDescription());
        this.setPrice(product.getPrice());
        this.setQuantity(product.getQuantity());
        this.setSize(product.getSize());
        this.setBrand(product.getBrand());
        this.setColor(product.getColor());
        this.setMaterial(product.getMaterial());
        this.setListingStartDate(product.getListingStartDate());
        this.setListingEndDate(product.getListingEndDate());
        this.setSecurityDeposit(product.getSecurityDeposit());
        this.setSubcategoryIds(product.getSubCategories().stream().map(SubCategory::getId).collect(Collectors.toList()));
        this.setCategoryIds(product.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
    }
}
