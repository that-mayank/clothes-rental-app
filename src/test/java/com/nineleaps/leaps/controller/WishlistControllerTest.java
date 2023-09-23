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
import java.lang.reflect.Field;
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
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);  // Initialize mocks

        // Create an instance of WishlistController and set the helper field using reflection
        wishlistController = new WishlistController(wishlistService, productService, helper);

        // Use reflection to set the helper field
        Field field = WishlistController.class.getDeclaredField("helper");
        field.setAccessible(true);
        field.set(wishlistController, helper);
    }

    @Test
    void addWishlist_productNotFound_shouldReturnNotFoundResponse() {
        // Arrange
        Long productId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Product is invalid"));
    }

    @Test
    void addWishlist_productAlreadyInWishlist_shouldReturnConflictResponse() {
        // Arrange
        Long productId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(productId);
        Wishlist wishlistItem = new Wishlist(product, user);
        List<Wishlist> wishlistItems = new ArrayList<>();
        wishlistItems.add(wishlistItem);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(Optional.of(product));
        when(wishlistService.readWishlist(user.getId())).thenReturn(wishlistItems);

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Product already in wishlist"));
    }

    @Test
    void addWishlist_productAddedToWishlist_shouldReturnCreatedResponse() {
        // Arrange
        Long productId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(productId);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.readProduct(productId)).thenReturn(Optional.of(product));
        when(wishlistService.readWishlist(user.getId())).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Added to wishlist"));
    }

    // Add more tests for positive and negative scenarios for addWishlist, getWishlist, and removeFromWishlist

    @Test
    void getWishlist_shouldReturnWishlistItems() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);

        List<Wishlist> wishlistItems = new ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        Wishlist wishlist = new Wishlist(product, user);
        wishlistItems.add(wishlist);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(wishlistService.readWishlist(user.getId())).thenReturn(wishlistItems);

        // Act
        ResponseEntity<List<ProductDto>> response = wishlistController.getWishlist(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Test Product", response.getBody().get(0).getName());
    }

    @Test
    void removeFromWishlist_shouldRemoveItem() {
        // Arrange
        Long productId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);

        when(helper.getUserFromToken(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.removeFromWishlist(productId, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Product removed successfully"));
    }
}
