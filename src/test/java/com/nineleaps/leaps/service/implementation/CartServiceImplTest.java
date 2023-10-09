package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.cart.AddToCartDto;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
import com.nineleaps.leaps.exceptions.CartItemAlreadyExistException;
import com.nineleaps.leaps.exceptions.CartItemNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.Cart;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Tag("unit_tests")
@DisplayName("CartService Tests")
class CartServiceImplTest {
    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Add To Cart: Product Already In Cart - Exception Thrown")
    void addToCart_ProductAlreadyInCart_ExceptionThrown() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1L, 1L,10, null, null);
        Product product = new Product();
        product.setId(1L);
        User user = new User();
        user.setId(1L);
        Cart existingCartItem = new Cart(product, user, 5, null, null, null);

        when(cartRepository.findByUserIdAndProductId(user.getId(), product.getId())).thenReturn(existingCartItem);

        // Act and Assert
        assertThrows(CartItemAlreadyExistException.class, () -> cartService.addToCart(addToCartDto, product, user));
    }

    @Test
    @DisplayName("Add To Cart: Quantity Out Of Bound - Exception Thrown")
    void addToCart_QuantityOutOfBound_ExceptionThrown() {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1L, 1L, 0, null, null);  // Assuming 0 is an invalid quantity
        Product product = new Product();
        product.setId(1L);
        User user = new User();
        user.setId(1L);

        // Act and Assert
        assertThrows(QuantityOutOfBoundException.class, () -> cartService.addToCart(addToCartDto, product, user));
    }



    @Test
    @DisplayName("Add To Cart: Successful Addition To Cart")
    void addToCart_SuccessfulAdditionToCart() throws CartItemAlreadyExistException, QuantityOutOfBoundException {
        // Arrange
        AddToCartDto addToCartDto = new AddToCartDto(1L,1L, 5, null, null);
        Product product = new Product();
        product.setId(1L);
        User user = new User();
        user.setId(1L);

        when(cartRepository.findByUserIdAndProductId(user.getId(), product.getId())).thenReturn(null);

        // Act
        cartService.addToCart(addToCartDto, product, user);

        // Assert
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("List Cart Items: Calculate Total Cost - Returns Correct Total Cost")
    void listCartItems_CalculateTotalCost_ReturnsCorrectTotalCost()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // Arrange
        User user = new User();
        user.setId(1L);  // Set the user ID

        Product product1 = new Product();
        product1.setPrice(20.0);

        Product product2 = new Product();
        product2.setPrice(15.0);

        LocalDateTime rentalStart1 = LocalDateTime.now().minusHours(2);
        LocalDateTime rentalEnd1 = LocalDateTime.now();


        LocalDateTime rentalStart2 = LocalDateTime.now().minusHours(3);
        LocalDateTime rentalEnd2 = LocalDateTime.now();


        Cart cart1 = new Cart();
        cart1.setQuantity(2);
        cart1.setProduct(product1);
        cart1.setUser(user);
        cart1.setRentalStartDate(rentalStart1);
        cart1.setRentalEndDate(rentalEnd1);

        Cart cart2 = new Cart();
        cart2.setQuantity(3);
        cart2.setProduct(product2);
        cart2.setUser(user);
        cart2.setRentalStartDate(rentalStart2);
        cart2.setRentalEndDate(rentalEnd2);

        List<Cart> cartList = List.of(cart1, cart2);
        when(cartRepository.findAllByUserOrderByCreateDateDesc(user)).thenReturn(cartList);

        // Get the private method "calculateTotalCost" with the appropriate parameters
        Method calculateTotalCostMethod = CartServiceImpl.class.getDeclaredMethod("calculateTotalCost", List.class);
        calculateTotalCostMethod.setAccessible(true);  // Make the method accessible

        // Invoke the private method
        List<CartItemDto> cartItems = cartList.stream().map(CartServiceImpl::getDtoFromCart).collect(Collectors.toList());
        double actualTotalCost = (double) calculateTotalCostMethod.invoke(cartService, cartItems);

        // Act
        CartDto cartDto = cartService.listCartItems(user);

        // Assert
        assertEquals(actualTotalCost, cartDto.getTotalCost(), 0.001); // Added delta for double comparison
    }







    @Test
    @DisplayName("List Cart Items: Calculate Tax - Returns Correct Tax")
    void listCartItems_CalculateTax_ReturnsCorrectTax() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        User user = new User();
        user.setId(1L);  // Set the user ID

        Product product1 = new Product();
        product1.setPrice(20.0);

        Product product2 = new Product();
        product2.setPrice(15.0);

        LocalDateTime rentalStart1 = LocalDateTime.now().minusHours(2);
        LocalDateTime rentalEnd1 = LocalDateTime.now();


        LocalDateTime rentalStart2 = LocalDateTime.now().minusHours(3);
        LocalDateTime rentalEnd2 = LocalDateTime.now();


        Cart cart1 = new Cart();
        cart1.setQuantity(2);
        cart1.setProduct(product1);
        cart1.setUser(user);
        cart1.setRentalStartDate(rentalStart1);
        cart1.setRentalEndDate(rentalEnd1);

        Cart cart2 = new Cart();
        cart2.setQuantity(3);
        cart2.setProduct(product2);
        cart2.setUser(user);
        cart2.setRentalStartDate(rentalStart2);
        cart2.setRentalEndDate(rentalEnd2);

        List<Cart> cartList = List.of(cart1, cart2);
        when(cartRepository.findAllByUserOrderByCreateDateDesc(user)).thenReturn(cartList);

        // Get the private method "calculateTotalCost" with the appropriate parameters
        Method calculateTotalCostMethod = CartServiceImpl.class.getDeclaredMethod("calculateTotalCost", List.class);
        calculateTotalCostMethod.setAccessible(true);  // Make the method accessible

        // Invoke the private method
        List<CartItemDto> cartItems = cartList.stream().map(CartServiceImpl::getDtoFromCart).collect(Collectors.toList());
        double actualTotalCost = (double) calculateTotalCostMethod.invoke(cartService, cartItems);



        // Calculate the expected tax (18% of the actual total cost)
        long roundedTax = Math.round(0.18 * actualTotalCost);
        double expectedTax = (double) roundedTax;

        // Act
        CartDto cartDto = cartService.listCartItems(user);

        // Assert
        assertEquals(expectedTax, cartDto.getTax(),0.001);
    }


    @Test
    @DisplayName("List Cart Items: Calculate Final Price - Returns Correct Final Price")
    void listCartItems_CalculateFinalPrice_ReturnsCorrectFinalPrice() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        User user = new User();
        user.setId(2L);  // Set the user ID

        Product product1 = new Product();
        product1.setPrice(20.0);


        // Set rentalStart1 to be the current date and rentalEnd1 to be 2 days from now
        LocalDateTime rentalStart1 = LocalDateTime.now();
        LocalDateTime rentalEnd1 = LocalDateTime.now().plusDays(2);


        Cart cart1 = new Cart();
        cart1.setQuantity(2);
        cart1.setProduct(product1);
        cart1.setUser(user);
        cart1.setRentalStartDate(rentalStart1);
        cart1.setRentalEndDate(rentalEnd1);



        List<Cart> cartList = List.of(cart1);
        when(cartRepository.findAllByUserOrderByCreateDateDesc(user)).thenReturn(cartList);

        // Get the private method "calculateTotalCost" with the appropriate parameters
        Method calculateTotalCostMethod = CartServiceImpl.class.getDeclaredMethod("calculateTotalCost", List.class);
        calculateTotalCostMethod.setAccessible(true);  // Make the method accessible

        // Invoke the private method
        List<CartItemDto> cartItems = cartList.stream().map(CartServiceImpl::getDtoFromCart).collect(Collectors.toList());
        double actualTotalCost = (double) calculateTotalCostMethod.invoke(cartService, cartItems);


        // Get the private method "calculateTax" with the appropriate parameters
        Method calculateTaxMethod = CartServiceImpl.class.getDeclaredMethod("calculateTax", double.class);
        calculateTaxMethod.setAccessible(true);  // Make the method accessible

        double expectedTax = (double) calculateTaxMethod.invoke(cartService, actualTotalCost);

        // Get the private method "calculateFinalPrice" with the appropriate parameters
        Method calculateFinalPriceMethod = CartServiceImpl.class.getDeclaredMethod("calculateFinalPrice", double.class, double.class);
        calculateFinalPriceMethod.setAccessible(true);  // Make the method accessible

        double expectedFinalPrice = (double) calculateFinalPriceMethod.invoke(cartService, actualTotalCost, expectedTax);


        // Act
        CartDto cartDto = cartService.listCartItems(user);
        double shippingCost = cartDto.getShippingCost();
        expectedFinalPrice += shippingCost;
        // Round the expected final price to one decimal place
        // Round the expected final price to the nearest whole number
        long roundedExpectedFinalPrice = Math.round(expectedFinalPrice);
        // Assert with a delta of 0.4

        assertEquals(roundedExpectedFinalPrice, cartDto.getFinalPrice(),0.4);

    }


    @Test
    @DisplayName("Update Cart Item: Successful Update")
    void testUpdateCartItem() throws CartItemNotExistException, QuantityOutOfBoundException {
        // Create a sample AddToCartDto
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(1L);
        addToCartDto.setQuantity(2);
        addToCartDto.setRentalStartDate(LocalDateTime.now());
        addToCartDto.setRentalEndDate(LocalDateTime.now().plusDays(2));

        // Create a sample User
        User user = new User();
        user.setId(1L);

        // Mock the cart item
        Cart cartItem = new Cart();
        cartItem.setQuantity(1);  // Initial quantity
        cartItem.setUser(user);
        cartItem.setProduct(new Product());  // You can set appropriate product details

        when(cartRepository.findByUserIdAndProductId(user.getId(), addToCartDto.getProductId())).thenReturn(cartItem);

        // Call the method to be tested
        assertDoesNotThrow(() -> cartService.updateCartItem(addToCartDto, user));

        // Verify that the cart item was updated
        verify(cartRepository).save(cartItem);
        assertEquals(addToCartDto.getQuantity(), cartItem.getQuantity());
        assertEquals(addToCartDto.getRentalStartDate(), cartItem.getRentalStartDate());
        assertEquals(addToCartDto.getRentalEndDate(), cartItem.getRentalEndDate());
    }



    @Test
    @DisplayName("Delete Cart Item: Successful Deletion")
    void testDeleteCartItem_SuccessfulDeletion() throws CartItemNotExistException {
        // Create a sample product ID and user
        Long productId = 1L;
        User user = new User();
        user.setId(1L);

        // Mock the cart item
        Cart cartItem = new Cart();
        cartItem.setId(1L);
        cartItem.setUser(user);
        when(cartRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(cartItem);

        // Call the method to be tested
        assertDoesNotThrow(() -> cartService.deleteCartItem(productId, user));

        // Verify that the cart item was deleted
        verify(cartRepository).deleteById(cartItem.getId());
    }

    @Test
    @DisplayName("Delete Cart Item: Cart Item Not Exist - CartItemNotExistException")
    void testDeleteCartItem_CartItemNotExistException() {
        // Create a sample non-existing product ID and user
        Long nonExistingProductId = 99L;
        User user = new User();
        user.setId(1L);

        // Mock the cart item as null (not found)
        when(cartRepository.findByUserIdAndProductId(user.getId(), nonExistingProductId)).thenReturn(null);

        // Call the method and expect CartItemNotExistException
        assertThrows(CartItemNotExistException.class, () -> cartService.deleteCartItem(nonExistingProductId, user));

        // Verify that the cartRepository.deleteById() is not called
        verify(cartRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Delete User Cart Items: Successful Deletion")
    void testDeleteUserCartItems_SuccessfulDeletion() {
        // Create a sample user
        User user = new User();
        user.setId(1L);

        // Call the method to be tested
        cartService.deleteUserCartItems(user);

        // Verify that the cartRepository.deleteByUser(user) is called
        verify(cartRepository).deleteByUser(user);
    }


    @Test
    @DisplayName("Update Product Quantity: Successful Update")
    void testUpdateProductQuantity_SuccessfulUpdate() throws CartItemNotExistException {
        // Create a sample product ID, user, and update DTO
        Long productId = 1L;
        User user = new User();
        user.setId(1L);

        UpdateProductQuantityDto updateDto = new UpdateProductQuantityDto();
        updateDto.setProductId(productId);
        updateDto.setQuantity(3);  // Updated quantity

        // Mock the cart item
        Cart cartItem = new Cart();
        cartItem.setId(1L);
        cartItem.setUser(user);
        when(cartRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(cartItem);

        // Call the method to be tested
        assertDoesNotThrow(() -> cartService.updateProductQuantity(updateDto, user));

        // Verify that the cartRepository.save() is called with the updated cart item
        ArgumentCaptor<Cart> cartArgumentCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartArgumentCaptor.capture());

        // Assert that the quantity was updated
        assertEquals(updateDto.getQuantity(), cartArgumentCaptor.getValue().getQuantity());
    }


    @Test
    @DisplayName("Update Product Quantity: Quantity Zero - Deletes Cart Item")
    void testUpdateProductQuantity_QuantityZero_DeletesCartItem() throws CartItemNotExistException {
        // Create a sample product ID, user, and update DTO with zero quantity
        Long productId = 1L;
        User user = new User();
        user.setId(1L);

        UpdateProductQuantityDto updateDto = new UpdateProductQuantityDto();
        updateDto.setProductId(productId);
        updateDto.setQuantity(0);  // Updated quantity

        // Mock the cart item
        Cart cartItem = new Cart();
        cartItem.setId(1L);
        cartItem.setUser(user);
        when(cartRepository.findByUserIdAndProductId(user.getId(), productId)).thenReturn(cartItem);

        // Call the method to be tested
        assertDoesNotThrow(() -> cartService.updateProductQuantity(updateDto, user));

        // Verify that the cartRepository.deleteById() is called with the correct product ID
        verify(cartRepository).deleteById(cartItem.getId());
    }

    @Test
    @DisplayName("Update Product Quantity: Cart Item Not Exist - CartItemNotExistException")
    void testUpdateProductQuantity_CartItemNotExistException() {
        // Create a sample non-existing product ID, user, and update DTO
        Long nonExistingProductId = 99L;
        User user = new User();
        user.setId(1L);

        UpdateProductQuantityDto updateDto = new UpdateProductQuantityDto();
        updateDto.setProductId(nonExistingProductId);
        updateDto.setQuantity(3);  // Updated quantity

        // Mock the cart item as null (not found)
        when(cartRepository.findByUserIdAndProductId(user.getId(), nonExistingProductId)).thenReturn(null);

        // Call the method and expect CartItemNotExistException
        assertThrows(CartItemNotExistException.class, () -> cartService.updateProductQuantity(updateDto, user));

        // Verify that cartRepository.save() is not called
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Calculate Total Cost Wrapper: Null Rental Dates")
    void testCalculateTotalCostWrapper_NullRentalDates() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Mock a CartItemDto with null rental start and end dates
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setQuantity(2);
        cartItemDto.setProduct(new Product());
        cartItemDto.setRentalStartDate(null);
        cartItemDto.setRentalEndDate(null);

        // Mock a list of CartItemDto
        List<CartItemDto> cartItems = Collections.singletonList(cartItemDto);

        // Use reflection to invoke the private calculateTotalCost method
        Method calculateTotalCostMethod = CartServiceImpl.class.getDeclaredMethod("calculateTotalCost", List.class);
        calculateTotalCostMethod.setAccessible(true);
        double actualTotalCost = (double) calculateTotalCostMethod.invoke(cartService, cartItems);

        // Calculate the expected total cost (assuming per hour rent is 1 for simplicity)
        double expectedTotalCost = cartItemDto.getProduct().getPrice() * cartItemDto.getQuantity();

        // Verify that the total cost is calculated correctly
        assertEquals(expectedTotalCost, actualTotalCost, 0.001);
    }

    @Test
    @DisplayName("Calculate Total Cost: Number Of Hours Zero - Set To 1")
    void testCalculateTotalCost_NumberOfHoursZero_SetTo1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Mock a CartItemDto with rental start and end dates, but with 0 hours
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();  // Assuming same date for simplicity
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setQuantity(2);
        cartItemDto.setProduct(new Product());
        cartItemDto.setRentalStartDate(startDate);
        cartItemDto.setRentalEndDate(endDate);

        // Mock a list of CartItemDto
        List<CartItemDto> cartItems = Collections.singletonList(cartItemDto);

        // Use reflection to invoke the private calculateTotalCost method
        Method calculateTotalCostMethod = CartServiceImpl.class.getDeclaredMethod("calculateTotalCost", List.class);
        calculateTotalCostMethod.setAccessible(true);
        double actualTotalCost = (double) calculateTotalCostMethod.invoke(cartService, cartItems);

        // Calculate expected total cost (assuming per hour rent is 1 for simplicity)
        double expectedTotalCost = cartItemDto.getProduct().getPrice() * cartItemDto.getQuantity();

        // Verify that the total cost is calculated correctly (hours should be set to 1)
        assertEquals(expectedTotalCost, actualTotalCost, 0.001);
    }



    @Test
    @DisplayName("Update Cart Item: Cart Item Not Exist - CartItemNotExistException")
    void testUpdateCartItem_CartItemNotExistException() throws CartItemNotExistException, QuantityOutOfBoundException {
        // Create a sample AddToCartDto
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(1L);
        addToCartDto.setQuantity(2);
        addToCartDto.setRentalStartDate(LocalDateTime.now());
        addToCartDto.setRentalEndDate(LocalDateTime.now().plusDays(2));

        // Create a sample User
        User user = new User();
        user.setId(1L);

        // Mock the cart item as null (not found)
        when(cartRepository.findByUserIdAndProductId(user.getId(), addToCartDto.getProductId())).thenReturn(null);

        // Call the method and expect CartItemNotExistException
        assertThrows(CartItemNotExistException.class, () -> cartService.updateCartItem(addToCartDto, user));

        // Verify that cartRepository.save() is not called
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update Cart Item: Quantity Zero - Deletes Cart Item")
    void testUpdateCartItem_QuantityZero_DeletesCartItem() throws CartItemNotExistException, QuantityOutOfBoundException {
        // Create a sample AddToCartDto with quantity zero
        AddToCartDto addToCartDto = new AddToCartDto();
        addToCartDto.setProductId(1L);
        addToCartDto.setQuantity(0);

        // Create a sample User
        User user = new User();
        user.setId(1L);

        // Mock the cart item
        Cart cartItem = new Cart();
        cartItem.setQuantity(1);  // Initial quantity
        cartItem.setUser(user);
        cartItem.setProduct(new Product());  // You can set appropriate product details

        when(cartRepository.findByUserIdAndProductId(user.getId(), addToCartDto.getProductId())).thenReturn(cartItem);

        // Call the method to be tested
        assertDoesNotThrow(() -> cartService.updateCartItem(addToCartDto, user));

        // Verify that the cart item was deleted
        verify(cartRepository).deleteById(cartItem.getId());
    }


}