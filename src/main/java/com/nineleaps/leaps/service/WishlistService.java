package com.nineleaps.leaps.service;

import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.repository.WishlistRepository;
import com.nineleaps.leaps.utils.Helper;
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

    @Override
    public void removeFromWishlist(Long userId, Product product) throws CustomException {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, product.getId());
        if(!Helper.notNull(wishlist)) {
            throw new CustomException("Item not found");
        }
        wishlistRepository.deleteById(wishlist.getId());
    }


}
