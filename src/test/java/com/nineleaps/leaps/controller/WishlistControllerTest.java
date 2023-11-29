//package com.nineleaps.leaps.controller;
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.common.ApiResponse;
//import com.nineleaps.leaps.dto.product.ProductDto;
//import com.nineleaps.leaps.model.product.Product;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.model.Wishlist;
//import com.nineleaps.leaps.service.ProductServiceInterface;
//import com.nineleaps.leaps.service.WishlistServiceInterface;
//import com.nineleaps.leaps.utils.Helper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Field;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//@Tag("unit_tests")
//@DisplayName("test case file for Wishlist Controller")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class WishlistControllerTest {
//
//    @Mock
//    private WishlistServiceInterface wishlistService;
//
//    @Mock
//    private ProductServiceInterface productService;
//
//    @Mock
//    private Helper helper;
//
//    @InjectMocks
//    private WishlistController wishlistController;
//
//    @BeforeEach
//    void setUp() throws IllegalAccessException, NoSuchFieldException {
//        MockitoAnnotations.openMocks(this);  // Initialize mocks
//
//        // Create an instance of WishlistController and set the helper field using reflection
//        wishlistController = new WishlistController(wishlistService, productService, helper);
//
//        // Use reflection to set the helper field
//        Field field = WishlistController.class.getDeclaredField("helper");
//        field.setAccessible(true);
//        field.set(wishlistController, helper);
//    }
//
//    @Test
//    @DisplayName("Add Wishlist - Product Not Found (Returns Not Found)")
//    void addWishlist_productNotFound_shouldReturnNotFoundResponse() {
//        // Arrange
//        Long productId = 1L;
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User();
//        user.setId(1L);
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.readProduct(productId)).thenReturn(Optional.empty());
//
//        // Act
//        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Product is invalid"));
//    }
//
//    @Test
//    @DisplayName("Add Wishlist - Product Already In Wishlist (Returns Conflict)")
//    void addWishlist_ProductAlreadyInWishlist_ReturnsConflict() {
//        Long productId = 1L; // Assuming a valid product ID
//        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest
//
//        User user = new User(); // Assuming a valid user
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.readProduct(productId)).thenReturn(Optional.of(new Product())); // Assuming product exists
//
//        // Mocking the wishlist to contain the product
//        Product existingProduct = new Product();
//        existingProduct.setId(productId);
//        Wishlist wishlist = new Wishlist(existingProduct, user);  // Product already in the wishlist
//        List<Wishlist> wishlistList = Collections.singletonList(wishlist);
//        when(wishlistService.readWishlist(user.getId())).thenReturn(wishlistList);
//
//        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);
//
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//        assertEquals("Product already in wishlist", Objects.requireNonNull(response.getBody()).getMessage());
//    }
//
//    @Test
//    @DisplayName("Add Wishlist - Product Not In Wishlist (Returns Created)")
//    void addWishlist_ProductNotInWishlist_ReturnsCreated() {
//        Long productId = 1L; // Assuming a valid product ID
//        HttpServletRequest request = mock(HttpServletRequest.class); // Mock HttpServletRequest
//
//        User user = new User(); // Assuming a valid user
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.readProduct(productId)).thenReturn(Optional.of(new Product())); // Assuming product exists
//
//        // Mocking an empty wishlist
//        when(wishlistService.readWishlist(user.getId())).thenReturn(Collections.emptyList());
//
//        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("Added to wishlist", Objects.requireNonNull(response.getBody()).getMessage());
//    }
//
//
//
//
//    @Test
//    @DisplayName("Add Wishlist - Product Already In Wishlist (Returns Conflict)")
//    void addWishlist_productAlreadyInWishlist_shouldReturnConflictResponse() {
//        // Arrange
//        Long productId = 1L;
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User();
//        user.setId(1L);
//        Product product = new Product();
//        product.setId(productId);
//        Wishlist wishlistItem = new Wishlist(product, user);
//        List<Wishlist> wishlistItems = new ArrayList<>();
//        wishlistItems.add(wishlistItem);
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.readProduct(productId)).thenReturn(Optional.of(product));
//        when(wishlistService.readWishlist(user.getId())).thenReturn(wishlistItems);
//
//        // Act
//        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Product already in wishlist"));
//    }
//
//
//    @Test
//    @DisplayName("Add Wishlist - Product Added To Wishlist (Returns Created)")
//    void addWishlist_productAddedToWishlist_shouldReturnCreatedResponse() {
//        // Arrange
//        Long productId = 1L;
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User();
//        user.setId(1L);
//        Product product = new Product();
//        product.setId(productId);
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.readProduct(productId)).thenReturn(Optional.of(product));
//        when(wishlistService.readWishlist(user.getId())).thenReturn(new ArrayList<>());
//
//        // Act
//        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Added to wishlist"));
//    }
//
//    // Add more tests for positive and negative scenarios for addWishlist, getWishlist, and removeFromWishlist
//
//    @Test
//    @DisplayName("Get Wishlist - Should Return Wishlist Items")
//    void getWishlist_shouldReturnWishlistItems() {
//        // Arrange
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User();
//        user.setId(1L);
//
//        List<Wishlist> wishlistItems = new ArrayList<>();
//        Product product = new Product();
//        product.setId(1L);
//        product.setName("Test Product");
//        Wishlist wishlist = new Wishlist(product, user);
//        wishlistItems.add(wishlist);
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(wishlistService.readWishlist(user.getId())).thenReturn(wishlistItems);
//
//        // Act
//        ResponseEntity<List<ProductDto>> response = wishlistController.getWishlist(request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
//        assertEquals("Test Product", response.getBody().get(0).getName());
//    }
//
//    @Test
//    @DisplayName("Remove From Wishlist - Should Remove Wishlist Item")
//    void removeFromWishlist_shouldRemoveItem() {
//        // Arrange
//        Long productId = 1L;
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User();
//        user.setId(1L);
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Act
//        ResponseEntity<ApiResponse> response = wishlistController.removeFromWishlist(productId, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("Product removed successfully"));
//    }
//}


package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        Product product = new Product();
        product.setId(productId);

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.addWishlist(productId, request);

        // Assert
        verify(wishlistService, times(1)).createWishlist(productId, request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }


    @Test
    void getWishlist_WithValidToken_ShouldReturnWishlist() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        when(helper.getUserFromToken(request)).thenReturn(user);
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
        when(helper.getUserFromToken(request)).thenReturn(user);
        doNothing().when(wishlistService).removeFromWishlist(request, productId);

        // Act
        ResponseEntity<ApiResponse> response = wishlistController.removeFromWishlist(productId, request);

        // Assert
        verify(wishlistService, times(1)).removeFromWishlist(request, productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
    }
}