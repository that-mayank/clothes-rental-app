package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;

    private WishlistServiceImpl wishlistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        wishlistService = new WishlistServiceImpl(wishlistRepository);
    }

    @Test
    void createWishlist() {
        // Prepare test data
        Wishlist wishlist = new Wishlist();

        // Perform createWishlist method
        wishlistService.createWishlist(wishlist);

        // Verify that the save method is called on the wishlistRepository
        verify(wishlistRepository).save(wishlist);
    }

    @Test
    void readWishlist() {
    // Prepare test data
        Product product1 = new Product();
        Product product2 = new Product();
        Long userId = 1L;
        Wishlist wishlist1 = new Wishlist();
        wishlist1.setProduct(product1);
        Wishlist wishlist2 = new Wishlist();
        wishlist2.setProduct(product2);
        List<Wishlist> wishlists = new ArrayList<>();
        wishlists.add(wishlist1);
        wishlists.add(wishlist2);
        // Set the 'deleted' flag for the product in wishlist2
        wishlist2.getProduct().setDeleted(true);

    // Mock the behavior of wishlistRepository
        when(wishlistRepository.findAllByUserIdOrderByCreateDateDesc(userId)).thenReturn(wishlists);

    // Perform readWishlist method
        List<Wishlist> result = wishlistService.readWishlist(userId);

    // Verify that the correct list of wishlists, excluding the deleted product, is returned
        assertEquals(1, result.size());
        assertEquals(wishlist1, result.get(0));
    }

    @Test
    void removeFromWishlist_itemFound() throws CustomException {
        // Prepare test data
        Long userId = 1L;
        Long productId = 1L;
        Wishlist wishlist = new Wishlist();
        wishlist.setId(1L);

        // Mock the behavior of wishlistRepository
        when(wishlistRepository.findByUserIdAndProductId(userId, productId)).thenReturn(wishlist);

        // Perform removeFromWishlist method
        assertDoesNotThrow(() -> wishlistService.removeFromWishlist(userId, productId));

        // Verify that the deleteById method is called on the wishlistRepository with the correct ID
        verify(wishlistRepository).deleteById(wishlist.getId());
    }

    @Test
    void removeFromWishlist_itemNotFound() {
        // Prepare test data
        Long userId = 1L;
        Long productId = 1L;

        // Mock the behavior of wishlistRepository
        when(wishlistRepository.findByUserIdAndProductId(userId, productId)).thenReturn(null);

        // Perform removeFromWishlist method and assert that it throws CustomException
        assertThrows(CustomException.class, () -> wishlistService.removeFromWishlist(userId, productId));

        // Verify that the deleteById method is not called on the wishlistRepository
        verify(wishlistRepository, never()).deleteById(anyLong());
    }
}
