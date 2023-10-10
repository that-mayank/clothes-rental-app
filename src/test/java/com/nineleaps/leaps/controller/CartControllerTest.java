package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
@Tag("unit_tests")
@DisplayName("Test case file for cart controller")
@ExtendWith(RuntimeBenchmarkExtension.class)
class CartControllerTest {

    @Mock
    private ProductServiceInterface productService;

    @Mock
    private CartServiceInterface cartService;

    @Mock
    private Helper helper;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }


    @Test
    @DisplayName("add to cart")
    void testAddToCart()  {
        // Prepare the AddToCartDto
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(123L);

        // Mock user and product
        User user = new User();  // Mock your user as needed
        Product product = new Product();  // Mock your product as needed

        // Mock dependencies
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(helper.getUserFromToken(request)).thenReturn(user);
        when(productService.getProductById(anyLong())).thenReturn(product);

        // Call the controller method
        ResponseEntity<ApiResponse> response = cartController.addToCart(addToCartDto, request);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Added to cart", Objects.requireNonNull(response.getBody()).getMessage());

        // Verify that the cartService.addToCart was called
        verify(cartService).addToCart(addToCartDto, product, user);
    }

    @Test
    @DisplayName("get cart items")
    void testGetCartItems() throws AuthenticationFailException {
        // Mock user and cartDto
        CartDto cartDto = new CartDto();
        // Assuming that you have a user object
        User user = new User();

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of cartService.listCartItems
        when(cartService.listCartItems(user)).thenReturn(cartDto);

        // Call the API method
        ResponseEntity<CartDto> responseEntity = cartController.getCartItems(mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(cartDto, responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete Cart items")
    void testDeleteCartItem() throws AuthenticationFailException {
        long productId = 1L; // valid product ID
        User user = new User();

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of cartService.deleteCartItem
        doNothing().when(cartService).deleteCartItem(productId, user);

        // Call the API method
        ResponseEntity<ApiResponse> responseEntity = cartController.deleteCartItem(productId, mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Item has been removed from cart successfully", Objects.requireNonNull(responseEntity.getBody()).getMessage());

        // Verify that cartService.deleteCartItem was called with the correct arguments
        verify(cartService).deleteCartItem(productId, user);
    }

    @Test
    @DisplayName("Update Cart Item")
    void testUpdateCartItem() throws AuthenticationFailException {
        AddToCartDto addToCartDto = new AddToCartDto(); // Replace with a valid DTO
        User user = new User(); // Replace with a valid user

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of cartService.updateCartItem
        doNothing().when(cartService).updateCartItem(addToCartDto, user);

        // Call the API method
        ResponseEntity<ApiResponse> responseEntity = cartController.updateCartItem(addToCartDto, mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Cart item has been updated", Objects.requireNonNull(responseEntity.getBody()).getMessage());

        // Verify that cartService.updateCartItem was called with the correct arguments
        verify(cartService).updateCartItem(addToCartDto, user);
    }

    @Test
    @DisplayName("Update Quantity")
    void testUpdateQuantity() throws AuthenticationFailException {
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto(); // Replace with a valid DTO
        User user = new User(); // Replace with a valid user

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of cartService.updateProductQuantity
        doNothing().when(cartService).updateProductQuantity(updateProductQuantityDto, user);

        // Call the API method
        ResponseEntity<ApiResponse> responseEntity = cartController.updateQuantity(updateProductQuantityDto, mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Product quantity has been updated successfully", Objects.requireNonNull(responseEntity.getBody()).getMessage());

        // Verify that cartService.updateProductQuantity was called with the correct arguments
        verify(cartService).updateProductQuantity(updateProductQuantityDto, user);
    }

}