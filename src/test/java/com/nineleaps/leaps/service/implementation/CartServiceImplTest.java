package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.CartItemAlreadyExistException;
import com.nineleaps.leaps.exceptions.CartItemNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addToCart_WhenCartItemDoesNotExist_ShouldAddToCart() throws CartItemAlreadyExistException, QuantityOutOfBoundException {
        //Arrange
        AddToCartDto addToCartDto = new AddToCartDto();
        Product product = new Product();
        User user = new User();

        when(cartRepository.findByUserIdAndProductId(user.getId(), product.getId())).thenReturn(null);

        //Act
        cartService.addToCart(addToCartDto, product, user);

        //Assert
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addToCart_WhenCartItemExists_ShouldThrowCartItemAlreadyExistException() throws CartItemAlreadyExistException {
        //Arrange
        AddToCartDto addToCartDto = new AddToCartDto();
        Product product = new Product();
        User user = new User();

        when(cartRepository.findByUserIdAndProductId(user.getId(), product.getId())).thenReturn(new Cart());

        //Act & Assert
        assertThrows(CartItemAlreadyExistException.class, () -> cartService.addToCart(addToCartDto, product, user));
    }

    @Test
    void listCartItems_ShouldReturnCartDtoWithCorrectValues() {
        //Arrange
        User user = new User();
        user.setId(1L);
        Cart cart1 = new Cart();
        cart1.setQuantity(2);
        cart1.setRentalStartDate(LocalDateTime.now());
        cart1.setRentalEndDate(LocalDateTime.now().plusHours(2));
        Product product1 = new Product();
        cart1.setProduct(product1);

        Cart cart2 = new Cart();
        cart2.setQuantity(1);
        Product product2 = new Product();
        cart2.setProduct(product2);

        List<Cart> cartList = List.of(cart1, cart2);

        when(cartRepository.findAllByUserOrderByCreateDateDesc(user)).thenReturn(cartList);

        //Act
        CartDto cartDto = cartService.listCartItems(user);

        // Assert
        assertNotNull(cartDto);
        assertEquals(2, cartDto.getCartItems().size());
        assertEquals(0, cartDto.getTotalCost());
        assertEquals(0, cartDto.getTax());
        assertEquals(100, cartDto.getFinalPrice()); //because shipping cost is 100 which is mandatory
    }

    @Test
    void deleteCartItem_WhenCartItemExists_ShouldDeleteCartItem() throws CartItemNotExistException {
        // Arrange
        Long productId = 1L;
        User user = new User();
        Cart cartItem = new Cart();
        cartItem.setId(1L);
        cartItem.setUser(user);

        when(cartRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(cartItem);

        // Act
        cartService.deleteCartItem(productId, user);

        // Assert
        verify(cartRepository, times(1)).deleteById(cartItem.getId());
    }

    @Test
    void deleteCartItem_WhenCartItemDoesNotExist_ShouldThrowCartItemNotExistException() throws CartItemNotExistException {
        // Arrange
        Long productId = 1L;
        User user = new User();

        when(cartRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(null);

        // Act & Assert
        assertThrows(CartItemNotExistException.class, () -> cartService.deleteCartItem(productId, user));
    }

    @Test
    void deleteUserCartItems_ShouldDeleteUserCartItems() {
        // Arrange
        User user = new User();

        // Act
        cartService.deleteUserCartItems(user);

        // Assert
        verify(cartRepository, times(1)).deleteByUser(user);
    }


    @Test
    void updateProductQuantity_WhenCartItemExistsAndQuantityIsGreaterThanZero_ShouldUpdateProductQuantity() throws CartItemNotExistException {
        // Arrange
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setProductId(1L);
        updateProductQuantityDto.setQuantity(2);
        User user = new User();
        Cart cartItem = new Cart();
        cartItem.setId(1L);
        cartItem.setUser(user);

        when(cartRepository.findByUserIdAndProductId(user.getId(), updateProductQuantityDto.getProductId())).thenReturn(cartItem);

        // Act
        cartService.updateProductQuantity(updateProductQuantityDto, user);

        // Assert
        verify(cartRepository, times(1)).save(cartItem);
        assertEquals(updateProductQuantityDto.getQuantity(), cartItem.getQuantity());
    }

    @Test
    void updateProductQuantity_WhenCartItemExistsAndQuantityIsZero_ShouldDeleteCartItem() throws CartItemNotExistException {
        // Arrange
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setProductId(1L);
        updateProductQuantityDto.setQuantity(0);
        User user = new User();
        Cart cartItem = new Cart();
        cartItem.setId(1L);
        cartItem.setUser(user);

        when(cartRepository.findByUserIdAndProductId(user.getId(), updateProductQuantityDto.getProductId())).thenReturn(cartItem);

        // Act
        cartService.updateProductQuantity(updateProductQuantityDto, user);

        // Assert
        verify(cartRepository, times(1)).deleteById(cartItem.getId());
    }

    @Test
    void updateProductQuantity_WhenCartItemDoesNotExist_ShouldThrowCartItemNotExistException() throws CartItemNotExistException {
        // Arrange
        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
        updateProductQuantityDto.setProductId(1L);
        updateProductQuantityDto.setQuantity(2);
        User user = new User();

        when(cartRepository.findByUserIdAndProductId(user.getId(), updateProductQuantityDto.getProductId())).thenReturn(null);

        // Act & Assert
        assertThrows(CartItemNotExistException.class, () -> cartService.updateProductQuantity(updateProductQuantityDto, user));
    }
}