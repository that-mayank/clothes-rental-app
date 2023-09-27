package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.service.WishlistServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistControllerTest {

    @Mock
    private WishlistServiceInterface wishlistService;
    @Mock
    private ProductServiceInterface productService;
    @Mock
    private Helper helper;
    @InjectMocks
    private WishlistController wishlistController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addWishlist_WithValidProduct_ShouldReturnCreatedStatus() {
        // Arrange
        long productId = 1L;
        String token = "valid_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(productId);
        Optional<Product> optionalProduct = Optional.of(product);
        when(helper.getUser(token)).thenReturn(user);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(productService.readProduct(productId)).thenReturn(optionalProduct);
        when(wishlistService.readWishlist(user.getId())).thenReturn(new ArrayList<>());
        doNothing().when(wishlistService).createWishlist(any(Wishlist.class));

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        verify(helper, times(1)).getUser(token);
        verify(productService, times(1)).readProduct(productId);
        verify(wishlistService, times(1)).readWishlist(user.getId());
        verify(wishlistService, times(1)).createWishlist(any(Wishlist.class));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void addWishlist_WithInvalidProduct_ShouldReturnNotFoundStatus() {
        // Arrange
        long productId = 1L;
        String token = "valid_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        when(helper.getUser(token)).thenReturn(user);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(productService.readProduct(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        verify(helper, times(1)).getUser(token);
        verify(productService, times(1)).readProduct(productId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void addWishlist_WithExistingProductInWishlist_ShouldReturnConflictStatus() {
        // Arrange
        long productId = 1L;
        String token = "valid_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(productId);
        Optional<Product> optionalProduct = Optional.of(product);
        Wishlist wishlistItem = new Wishlist(product, user);
        List<Wishlist> wishlistItems = new ArrayList<>();
        wishlistItems.add(wishlistItem);
        when(helper.getUser(token)).thenReturn(user);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(productService.readProduct(productId)).thenReturn(optionalProduct);
        when(wishlistService.readWishlist(user.getId())).thenReturn(wishlistItems);

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        verify(helper, times(1)).getUser(token);
        verify(productService, times(1)).readProduct(productId);
        verify(wishlistService, times(1)).readWishlist(user.getId());
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void getWishlist_WithValidToken_ShouldReturnWishlist() {
        // Arrange
        String token = "valid_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        when(helper.getUser(token)).thenReturn(user);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        List<Wishlist> wishlistItems = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        wishlistItems.add(new Wishlist(product1, user));
        wishlistItems.add(new Wishlist(product2, user));
        when(wishlistService.readWishlist(user.getId())).thenReturn(wishlistItems);

        // Act
        ResponseEntity<List<ProductDto>> response = wishlistController.getWishlist(request);

        // Assert
        verify(helper, times(1)).getUser(token);
        verify(wishlistService, times(1)).readWishlist(user.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void removeFromWishlist_WithValidProductAndToken_ShouldReturnOkStatus() {
        // Arrange
        long productId = 1L;
        String token = "valid_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        when(helper.getUser(token)).thenReturn(user);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        doNothing().when(wishlistService).removeFromWishlist(user.getId(), productId);

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.removeFromWishlist(productId, request);

        // Assert
        verify(helper, times(1)).getUser(token);
        verify(wishlistService, times(1)).removeFromWishlist(user.getId(), productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }
}