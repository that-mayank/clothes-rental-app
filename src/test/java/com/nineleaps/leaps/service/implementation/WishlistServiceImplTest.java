package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.WishlistRepository;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Tag("unit")
class WishlistServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private Helper helper;

    @Mock
    private ProductServiceInterface productService;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createWishlist_ProductExistsAndNotInWishlist_ShouldCreateWishlistItem() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(helper.getUser(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(Optional.of(product));
        when(wishlistRepository.findAllByUserIdOrderByCreateDateDesc(user.getId())).thenReturn(new ArrayList<>());

        // Act
        assertDoesNotThrow(() -> wishlistService.createWishlist(productId, request));

        // Assert
        verify(wishlistRepository, times(1)).save(any());
    }

    @Test
    void createWishlist_ProductDoesNotExist_ShouldThrowProductNotExistException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        Long productId = 1L;

        when(productService.readProduct(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotExistException.class, () -> wishlistService.createWishlist(productId, request));

        // Assert
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void readWishlist_ShouldReturnListOfProductsInWishlist() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<Wishlist> wishlists = new ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        wishlists.add(new Wishlist(product, user));

        when(helper.getUser(request)).thenReturn(user);
        when(wishlistRepository.findAllByUserIdOrderByCreateDateDesc(user.getId())).thenReturn(wishlists);

        // Act
        List<ProductDto> result = wishlistService.readWishlist(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void removeFromWishlist_WishlistItemExists_ShouldRemoveFromWishlist() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long productId = 1L;
        Wishlist wishlist = new Wishlist(new Product(), user);
        wishlist.setId(1L);

        when(helper.getUser(request)).thenReturn(user);
        when(wishlistRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(wishlist);

        // Act
        assertDoesNotThrow(() -> wishlistService.removeFromWishlist(request, productId));

        // Assert
        verify(wishlistRepository, times(1)).deleteById(wishlist.getId());
    }

    @Test
    void removeFromWishlist_WishlistItemDoesNotExist_ShouldThrowCustomException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long productId = 1L;

        when(helper.getUser(request)).thenReturn(user);
        when(wishlistRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(null);

        // Act & Assert
        assertThrows(CustomException.class, () -> wishlistService.removeFromWishlist(request, productId));

        // Assert
        verify(wishlistRepository, never()).deleteById(anyLong());
    }
}
