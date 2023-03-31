package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Category;

import java.util.List;

public interface ProductServiceInterface {
    public void addProduct(ProductDto productDto, Category category);
    public List<ProductDto> listProducts();
    public void updateProduct(Long productId, ProductDto productDto, Category category);
}
