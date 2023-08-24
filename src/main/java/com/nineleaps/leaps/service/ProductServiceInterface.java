package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;

import java.util.List;
import java.util.Optional;

public interface ProductServiceInterface {
    void addProduct(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user);

    List<ProductDto> listProducts(int pageNumber, int pageSize, User user);

    void updateProduct(Long productId, ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user);

    Optional<Product> readProduct(Long productId);

    List<ProductDto> listProductsById(Long subcategoryId, User user);

    List<ProductDto> listProductsByCategoryId(Long categoryId, User user);

    ProductDto listProductByid(Long productId);

    Product getProductById(Long productId);

    List<ProductDto> listProductsDesc(User user);

    List<ProductDto> listOwnerProducts(User user);

    List<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice);

    List<ProductDto> searchProducts(String query, User user);

    List<ProductDto> filterProducts(String size, Long subcategoryId, double minPrice, double maxPrice);

    void deleteProduct(Long productId, Long userId);

    Product getProduct(Long productId, Long userId);

    void disableProduct(Product product, int quantity);

    void enableProduct(Product product, int quantity);
}
