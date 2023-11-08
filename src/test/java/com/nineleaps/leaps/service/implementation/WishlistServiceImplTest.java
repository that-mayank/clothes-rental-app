package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createWishlist() {
        // Prepare test data
        Long productId = 1L;
        Wishlist wishlist = new Wishlist();

        // Perform createWishlist method
        wishlistService.createWishlist(productId, request);

        // Verify that the save method is called on the wishlistRepository
        verify(wishlistRepository).save(wishlist);
    }

    @Test
    void readWishlist() {
    // Prepare test data
        Product product1 = new Product();
        Product product2 = new Product();
        Long userId = 1L;
        ProductDto wishlist1 = new ProductDto(product1);
        ProductDto wishlist2 = new ProductDto(product2);
        List<Wishlist> wishlists = new ArrayList<>();
        wishlists.add(new Wishlist());
        wishlists.add(new Wishlist());

    // Mock the behavior of wishlistRepository
        when(wishlistRepository.findAllByUserIdOrderByCreateDateDesc(userId)).thenReturn(wishlists);

    // Perform readWishlist method
        List<ProductDto> result = wishlistService.readWishlist(request);

    // Verify that the correct list of wishlists, excluding the deleted product, is returned
        assertEquals(2, result.size());
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
        assertDoesNotThrow(() -> wishlistService.removeFromWishlist(request, productId));

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
        assertThrows(CustomException.class, () -> wishlistService.removeFromWishlist(request, productId));

        // Verify that the deleteById method is not called on the wishlistRepository
        verify(wishlistRepository, never()).deleteById(anyLong());
    }
}
