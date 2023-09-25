package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistServiceImplTest {

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @Mock
    private WishlistRepository wishlistRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createWishlist_WishlistCreatedSuccessfully() {
        // Arrange
        Wishlist wishlist = new Wishlist();

        // Act
        wishlistService.createWishlist(wishlist);

        // Assert
        verify(wishlistRepository, times(1)).save(wishlist);
    }

    @Test
    void readWishlist_ReturnWishlistForUserId() {
        // Arrange
        Long userId = 123L;
        Wishlist wishlist1 = new Wishlist();
        Product product1 = new Product();
        product1.setDeleted(false);
        wishlist1.setProduct(product1);

        Wishlist wishlist2 = new Wishlist();
        Product product2 = new Product();
        product2.setDeleted(true);  // Set product as deleted
        wishlist2.setProduct(product2);

        List<Wishlist> wishlistList = new ArrayList<>();
        wishlistList.add(wishlist1);
        wishlistList.add(wishlist2);

        // Mock repository method
        when(wishlistRepository.findAllByUserIdOrderByCreateDateDesc(userId)).thenReturn(wishlistList);

        // Act
        List<Wishlist> result = wishlistService.readWishlist(userId);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(wishlist1));
        assertFalse(result.contains(wishlist2));
    }


    @Test
    void removeFromWishlist_ItemRemovedSuccessfully() throws CustomException {
        // Arrange
        Long userId = 123L;
        Long productId = 456L;
        Wishlist wishlist = new Wishlist();
        wishlist.setId(1L);

        // Mock repository method
        when(wishlistRepository.findByUserIdAndProductId(userId, productId)).thenReturn(wishlist);

        // Act
        wishlistService.removeFromWishlist(userId, productId);

        // Assert
        verify(wishlistRepository, times(1)).deleteById(wishlist.getId());
    }

    @Test
    void removeFromWishlist_WishlistItemNotFound_ThrowsCustomException() {
        // Arrange
        Long userId = 123L;
        Long productId = 456L;

        // Mock repository method to return null (item not found)
        when(wishlistRepository.findByUserIdAndProductId(userId, productId)).thenReturn(null);

        // Act and Assert
        assertThrows(CustomException.class, () -> wishlistService.removeFromWishlist(userId, productId));
    }
}
