package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.model.product.Product;
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
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addWishlist_WithValidProduct_ShouldReturnCreatedStatus() {
        // Arrange
        long productId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(productId);
        Optional<Product> optionalProduct = Optional.of(product);
        when(helper.getUser(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(optionalProduct);
        when(wishlistService.readWishlist(request)).thenReturn(new ArrayList<>());
        doNothing().when(wishlistService).createWishlist(productId, request);

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        verify(productService, times(1)).readProduct(productId);
        verify(wishlistService, times(1)).readWishlist(request);
        verify(wishlistService, times(1)).createWishlist(productId, request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void addWishlist_WithInvalidProduct_ShouldReturnNotFoundStatus() {
        // Arrange
        long productId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        when(helper.getUser(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        verify(productService, times(1)).readProduct(productId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void addWishlist_WithExistingProductInWishlist_ShouldReturnConflictStatus() {
        // Arrange
        long productId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(productId);
        Optional<Product> optionalProduct = Optional.of(product);
        ProductDto wishlistItem = new ProductDto(product);
        List<ProductDto> wishlistItems = new ArrayList<>();
        wishlistItems.add(wishlistItem);
        when(helper.getUser(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(optionalProduct);
        when(wishlistService.readWishlist(request)).thenReturn(wishlistItems);

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        verify(productService, times(1)).readProduct(productId);
        verify(wishlistService, times(1)).readWishlist(request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
    }

    @Test
    void getWishlist_WithValidToken_ShouldReturnWishlist() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        when(helper.getUser(request)).thenReturn(user);
        List<ProductDto> wishlistItems = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        wishlistItems.add(new ProductDto(product1));
        wishlistItems.add(new ProductDto(product2));
        when(wishlistService.readWishlist(request)).thenReturn(wishlistItems);

        // Act
        ResponseEntity<List<ProductDto>> response = wishlistController.getWishlist(request);

        // Assert
        verify(wishlistService, times(1)).readWishlist(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void removeFromWishlist_WithValidProductAndToken_ShouldReturnOkStatus() {
        // Arrange
        long productId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        when(helper.getUser(request)).thenReturn(user);
        doNothing().when(wishlistService).removeFromWishlist(request, productId);

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.removeFromWishlist(productId, request);

        // Assert
        verify(wishlistService, times(1)).removeFromWishlist(request, productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }
}