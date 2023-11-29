package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface ProductServiceInterface {
    void addProduct(ProductDto productDto, HttpServletRequest request);

    List<ProductDto> listProducts(int pageNumber, int pageSize, HttpServletRequest request);

    void updateProduct(Long productId, ProductDto productDto, HttpServletRequest request);

    Optional<Product> readProduct(Long productId);

    List<ProductDto> listProductsById(Long subcategoryId, HttpServletRequest request);

    List<ProductDto> listProductsByCategoryId(Long categoryId, HttpServletRequest request);

    ProductDto listProductByid(Long productId);

    Product getProductById(Long productId);

    List<ProductDto> listProductsDesc(HttpServletRequest request);

    List<ProductDto> listOwnerProducts(HttpServletRequest request);

    List<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice);

    List<ProductDto> searchProducts(String query, HttpServletRequest request);

    List<ProductDto> filterProducts(String size, Long subcategoryId, double minPrice, double maxPrice);

    void deleteProduct(Long productId, HttpServletRequest request);

    Product getProduct(Long productId, Long userId);

    void disableProduct(Long productId, int quantity, HttpServletRequest request);

    void enableProduct(Long productId, int quantity, HttpServletRequest request);
}