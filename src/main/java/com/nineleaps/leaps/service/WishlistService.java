package com.nineleaps.leaps.service;

import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.repository.WishlistRepository;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<Wishlist> wishlists = wishlistRepository.findAllByUserIdOrderByCreateDateDesc(userId);
        List<Wishlist> body = new ArrayList<>();
        for (Wishlist wishlist : wishlists) {
            if (!wishlist.getProduct().isDeleted()) {
                body.add(wishlist);
            }
        }
        return body;
    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) throws CustomException {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (!Helper.notNull(wishlist)) {
            throw new CustomException("Item not found");
        }
        wishlistRepository.deleteById(wishlist.getId());

    }

}
