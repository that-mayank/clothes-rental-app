package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService implements WishlistServiceInterface {
    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public void createWishlist(Wishlist wishlist) {
        wishlistRepository.save(wishlist);
    }

    @Override
    public List<Wishlist> readWishlist(Long userId) {
        return wishlistRepository.findAllByUserIdOrderByCreateDateDesc(userId);
    }
}
