package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;

import java.util.List;
import java.util.Optional;

public interface ProductServiceInterface {
    void addProduct(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user);

    List<ProductDto> listProducts(int pageNumber, int pageSize);

    void updateProduct(Long productId, ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user);

    Optional<Product> readProduct(Long productId);

    List<ProductDto> listProductsById(Long subcategoryId);

    List<ProductDto> listProductsByCategoryId(Long categoryId);

    ProductDto listProductByid(Long productId);

    Product getProductById(Long productId);

    List<ProductDto> listProductsDesc(User user);

    List<ProductDto> listOwnerProducts(User user);

    List<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice);

    List<ProductDto> searchProducts(String query);
}
