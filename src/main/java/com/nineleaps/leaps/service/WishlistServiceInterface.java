package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.Wishlist;

import java.util.List;

public interface WishlistServiceInterface {
    void createWishlist(Wishlist wishlist);

    List<Wishlist> readWishlist(Long userId);

    void removeFromWishlist(Long userId, Long productId);
}
