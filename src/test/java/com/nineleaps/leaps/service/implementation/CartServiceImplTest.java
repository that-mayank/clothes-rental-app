package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.CartItemAlreadyExistException;
import com.nineleaps.leaps.exceptions.CartItemNotExistException;
import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.CartRepository;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Tag("unit")
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private Helper helper;

    @Mock
    private ProductServiceInterface productService;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addToCart_ProductNotAlreadyInCart_ShouldAddToCart() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Product product = new Product();
        product.setId(1L);
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(1L);
        addToCartDto.setQuantity(2);
        addToCartDto.setRentalStartDate(LocalDateTime.now());
        addToCartDto.setRentalEndDate(LocalDateTime.now().plusDays(2));

        when(helper.getUser(request)).thenReturn(user);
        when(productService.getProductById(addToCartDto.getProductId())).thenReturn(product);
        when(cartRepository.findByUserIdAndProductId(user.getId(), product.getId())).thenReturn(null);

        // Act
        assertDoesNotThrow(() -> cartService.addToCart(addToCartDto, request));

        // Assert
        verify(cartRepository, times(1)).save(any());
    }

    @Test
    void addToCart_ProductAlreadyInCart_ShouldThrowException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Product product = new Product();
        product.setId(1L);
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(1L);
        addToCartDto.setQuantity(2);
        addToCartDto.setRentalStartDate(LocalDateTime.now());
        addToCartDto.setRentalEndDate(LocalDateTime.now().plusDays(2));

        when(helper.getUser(request)).thenReturn(user);
        when(productService.getProductById(addToCartDto.getProductId())).thenReturn(product);
        when(cartRepository.findByUserIdAndProductId(user.getId(), product.getId())).thenReturn(new Cart());

        // Act & Assert
        assertThrows(CartItemAlreadyExistException.class, () -> cartService.addToCart(addToCartDto, request));

        // Assert
        verify(cartRepository, never()).save(any());
    }

    @Test
    void listCartItems_ShouldReturnCartDto() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(1L);
        product.setPrice(100.0);
        product.setUser(user);
        List<Cart> cartList = new ArrayList<>();
        Cart cartItem = new Cart();
        cartItem.setQuantity(2);
        cartItem.setRentalStartDate(LocalDateTime.now());
        cartItem.setRentalEndDate(LocalDateTime.now().plusDays(2));
        cartItem.setProduct(product);
        cartList.add(cartItem);

        when(helper.getUser(request)).thenReturn(user);
        when(cartRepository.findAllByUserOrderByCreateDateDesc(user)).thenReturn(cartList);

        // Act
        CartDto result = cartService.listCartItems(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCartItems().size());
    }

    // Add more test cases for deleteCartItem, deleteUserCartItems, and updateProductQuantity methods.

    @Test
    void deleteCartItem_CartItemNotExist_ShouldThrowException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long productId = 1L;

        when(helper.getUser(request)).thenReturn(user);
        when(cartRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(null);

        // Act & Assert
        assertThrows(CartItemNotExistException.class, () -> cartService.deleteCartItem(productId, request));

        // Assert
        verify(cartRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteCartItem_CartItemExists_ShouldDeleteCartItem() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long productId = 1L;
        Cart cartItem = new Cart();
        cartItem.setId(1L);

        when(helper.getUser(request)).thenReturn(user);
        when(cartRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(cartItem);

        // Act
        assertDoesNotThrow(() -> cartService.deleteCartItem(productId, request));

        // Assert
        verify(cartRepository, times(1)).deleteById(cartItem.getId());
    }

    @Test
    void deleteUserCartItems_ShouldDeleteAllCartItemsForUser() {
        // Arrange
        User user = new User();

        // Act
        assertDoesNotThrow(() -> cartService.deleteUserCartItems(user));

        // Assert
        verify(cartRepository, times(1)).deleteByUser(user);
    }

    @Test
    void updateProductQuantity_CartItemNotExist_ShouldThrowException() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setProductId(1L);
        updateProductQuantityDto.setQuantity(2);

        when(helper.getUser(request)).thenReturn(user);
        when(cartRepository.findByUserIdAndProductId(user.getId(), updateProductQuantityDto.getProductId())).thenReturn(null);

        // Act & Assert
        assertThrows(CartItemNotExistException.class, () ->
                cartService.updateProductQuantity(updateProductQuantityDto, request));

        // Assert
        verify(cartRepository, never()).save(any());
        verify(cartRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateProductQuantity_UpdatedQuantityZero_ShouldDeleteCartItem() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setProductId(1L);
        updateProductQuantityDto.setQuantity(0);
        Cart cartItem = new Cart();
        cartItem.setId(1L);

        when(helper.getUser(request)).thenReturn(user);
        when(cartRepository.findByUserIdAndProductId(user.getId(), updateProductQuantityDto.getProductId())).thenReturn(cartItem);

        // Act
        assertDoesNotThrow(() -> cartService.updateProductQuantity(updateProductQuantityDto, request));

        // Assert
        verify(cartRepository, times(1)).deleteById(cartItem.getId());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void updateProductQuantity_UpdatedQuantityGreaterThanZero_ShouldUpdateQuantity() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setProductId(1L);
        updateProductQuantityDto.setQuantity(2);
        Cart cartItem = new Cart();
        cartItem.setId(1L);

        when(helper.getUser(request)).thenReturn(user);
        when(cartRepository.findByUserIdAndProductId(user.getId(), updateProductQuantityDto.getProductId())).thenReturn(cartItem);

        // Act
        assertDoesNotThrow(() -> cartService.updateProductQuantity(updateProductQuantityDto, request));

        // Assert
        verify(cartRepository, times(1)).save(cartItem);
        verify(cartRepository, never()).deleteById(anyLong());
    }
}
