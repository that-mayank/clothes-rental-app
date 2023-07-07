package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.CartServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @Mock
    private CartServiceInterface cartService;

    @Mock
    private ProductServiceInterface productService;

    @Mock
    private Helper helper;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addToCart_ValidAddToCartDto_ReturnsCreatedResponse() throws AuthenticationFailException, ProductNotExistException, QuantityOutOfBoundException {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Product product = new Product();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);
        when(productService.getProductById(eq(addToCartDto.getProductId()))).thenReturn(product);

        // Act
        ResponseEntity<ApiResponse> responseEntity = cartController.addToCart(addToCartDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Added to cart", response.getMessage());

        ArgumentCaptor<AddToCartDto> addToCartDtoArgumentCaptor = ArgumentCaptor.forClass(AddToCartDto.class);
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(cartService).addToCart(addToCartDtoArgumentCaptor.capture(), productArgumentCaptor.capture(), userArgumentCaptor.capture());
        assertEquals(addToCartDto, addToCartDtoArgumentCaptor.getValue());
        assertEquals(product, productArgumentCaptor.getValue());
        assertEquals(user, userArgumentCaptor.getValue());
    }

    @Test
    void getCartItems_ReturnsCartDto() throws AuthenticationFailException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        CartDto cartDto = new CartDto();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);
        when(cartService.listCartItems(eq(user))).thenReturn(cartDto);

        // Act
        ResponseEntity<CartDto> responseEntity = cartController.getCartItems(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        CartDto resultCartDto = responseEntity.getBody();
        assertNotNull(resultCartDto);
        assertEquals(cartDto, resultCartDto);
    }

    @Test
    void updateCartItem_ValidAddToCartDto_ReturnsOkResponse() throws AuthenticationFailException {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = cartController.updateCartItem(addToCartDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Cart item has been updated", response.getMessage());

        ArgumentCaptor<AddToCartDto> addToCartDtoArgumentCaptor = ArgumentCaptor.forClass(AddToCartDto.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(cartService).updateCartItem(addToCartDtoArgumentCaptor.capture(), userArgumentCaptor.capture());
        assertEquals(addToCartDto, addToCartDtoArgumentCaptor.getValue());
        assertEquals(user, userArgumentCaptor.getValue());
    }

    @Test
    void deleteCartItem_ValidProductId_ReturnsOkResponse() throws AuthenticationFailException {
        // Arrange
        Long productId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = cartController.deleteCartItem(productId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Item has been removed from cart successfully", response.getMessage());

        verify(cartService).deleteCartItem(eq(productId), eq(user));
    }

    @Test
    void updateQuantity_ValidUpdateProductQuantityDto_ReturnsOkResponse() {
        // Arrange
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();

        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer token");
        when(helper.getUser(eq("token"))).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = cartController.updateQuantity(updateProductQuantityDto, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Product quantity has been updated successfully", response.getMessage());

        ArgumentCaptor<UpdateProductQuantityDto> updateProductQuantityDtoArgumentCaptor = ArgumentCaptor.forClass(UpdateProductQuantityDto.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(cartService).updateProductQuantity(updateProductQuantityDtoArgumentCaptor.capture(), userArgumentCaptor.capture());
        assertEquals(updateProductQuantityDto, updateProductQuantityDtoArgumentCaptor.getValue());
        assertEquals(user, userArgumentCaptor.getValue());
    }
}