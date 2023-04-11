package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.Wishlist;

import java.util.List;

public interface WishlistServiceInterface {
    public void createWishlist(Wishlist wishlist);

    List<Wishlist> readWishlist(Long userId);
}