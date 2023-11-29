package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.product.ProductDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface WishlistServiceInterface {
    void createWishlist(Long productId, HttpServletRequest request);

    List<ProductDto> readWishlist(HttpServletRequest request);

    void removeFromWishlist(HttpServletRequest request, Long productId);
}