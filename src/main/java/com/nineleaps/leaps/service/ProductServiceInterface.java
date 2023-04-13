package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;

import java.util.List;
import java.util.Optional;

public interface ProductServiceInterface {
    public void addProduct(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories);

    public List<ProductDto> listProducts();

    public void updateProduct(Long productId, ProductDto productDto, List<SubCategory> subCategories, List<Category> categories);

    public Optional<Product> readProduct(Long productId);

    public List<Product> listProductsById(Long subcategoryId);

    public List<ProductDto> listProductsByCategoryId(Long categoryId);

    public ProductDto listProductByid(Long productId);

    public Product getProductById(Long productId);
}
