package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.repository.WishlistRepository;
import com.nineleaps.leaps.service.WishlistServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service // Marks this class as a Spring service component
@AllArgsConstructor // Lombok annotation to generate a constructor with all required fields
@Transactional // Marks this class as transactional for database operations
public class WishlistServiceImpl implements WishlistServiceInterface {

    private final WishlistRepository wishlistRepository;

    // Method to create a new wishlist item
    @Override
    public void createWishlist(Wishlist wishlist) {
        wishlistRepository.save(wishlist);
    }

    // Method to read the wishlist items for a given user
    @Override
    public List<Wishlist> readWishlist(Long userId) {
        // Retrieve wishlists for the user and sort them by creation date in descending order
        List<Wishlist> wishlists = wishlistRepository.findAllByUserIdOrderByCreateDateDesc(userId);

        // Filter out deleted products from the wishlist
        List<Wishlist> filteredWishlist = new ArrayList<>();
        for (Wishlist wishlist : wishlists) {
            if (!wishlist.getProduct().isDeleted()) {
                filteredWishlist.add(wishlist);
            }
        }
        return filteredWishlist;
    }

    // Method to remove an item from the wishlist
    @Override
    public void removeFromWishlist(Long userId, Long productId) throws CustomException {
        // Find the wishlist item by user ID and product ID
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId);

        // Check if the wishlist item exists
        if (!Helper.notNull(wishlist)) {
            throw new CustomException("Item not found");
        }

        // Delete the wishlist item from the database
        wishlistRepository.deleteById(wishlist.getId());
    }
}
