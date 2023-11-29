//package com.nineleaps.leaps.controller;
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.common.ApiResponse;
//import com.nineleaps.leaps.dto.cart.AddToCartDto;
//import com.nineleaps.leaps.dto.cart.CartDto;
//import com.nineleaps.leaps.dto.cart.UpdateProductQuantityDto;
//import com.nineleaps.leaps.exceptions.AuthenticationFailException;
//import com.nineleaps.leaps.exceptions.ProductNotExistException;
//import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.model.product.Product;
//import com.nineleaps.leaps.service.CartServiceInterface;
//import com.nineleaps.leaps.service.ProductServiceInterface;
//import com.nineleaps.leaps.utils.Helper;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpServletRequest;
//
//import javax.servlet.http.HttpServletRequest;
//
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//@Tag("unit_tests")
//@DisplayName("Test case file for cart controller")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class CartControllerTest {
//
//    @Mock
//    private ProductServiceInterface productService;
//
//    @Mock
//    private CartServiceInterface cartService;
//
//    @Mock
//    private Helper helper;
//
//    @InjectMocks
//    private CartController cartController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//    }
//
//
//    @Test
//    @DisplayName("add to cart")
//    void testAddToCart()  {
//        // Prepare the AddToCartDto
//        AddToCartDto addToCartDto = new AddToCartDto();
//        addToCartDto.setProductId(123L);
//
//        // Mock user and product
//        User user = new User();  // Mock your user as needed
//        Product product = new Product();  // Mock your product as needed
//
//        // Mock dependencies
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        when(productService.getProductById(anyLong())).thenReturn(product);
//
//        // Call the controller method
//        ResponseEntity<ApiResponse> response = cartController.addToCart(addToCartDto, request);
//
//        // Verify the response
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("Added to cart", Objects.requireNonNull(response.getBody()).getMessage());
//
//        // Verify that the cartService.addToCart was called
//        verify(cartService).addToCart(addToCartDto, product, user);
//    }
//
//    @Test
//    void testAddToCartCatchBlock() throws AuthenticationFailException, ProductNotExistException, QuantityOutOfBoundException {
//        // Create a sample AddToCartDto
//        AddToCartDto addToCartDto = new AddToCartDto();
//        addToCartDto.setProductId(1L); // Set a product ID
//
//        User user = new User();
//        user.setId(1L);
//
//        // Mock the helper.getUserFromToken to return a User
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock productService.getProductById to throw a simulated exception
//        doAnswer(invocation -> {
//            throw new ProductNotExistException("Simulated ProductNotExistException");
//        }).when(productService).getProductById(1L);
//
//        // Call the addToCart method and capture the response
//        ResponseEntity<ApiResponse> response = cartController.addToCart(addToCartDto, new MockHttpServletRequest());
//
//        // Assert that the response status code is INTERNAL_SERVER_ERROR
//        // and the message in the ApiResponse is as expected
//        ResponseEntity<ApiResponse> expectedResponse = new ResponseEntity<>(new ApiResponse(false, "Failed to add to cart"), HttpStatus.INTERNAL_SERVER_ERROR);
//        Assertions.assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
//    }
//    @Test
//    @DisplayName("get cart items")
//    void testGetCartItems() throws AuthenticationFailException {
//        // Mock user and cartDto
//        CartDto cartDto = new CartDto();
//        // Assuming that you have a user object
//        User user = new User();
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of cartService.listCartItems
//        when(cartService.listCartItems(user)).thenReturn(cartDto);
//
//        // Call the API method
//        ResponseEntity<CartDto> responseEntity = cartController.getCartItems(mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(cartDto, responseEntity.getBody());
//    }
//
//    @Test
//    void testGetCartItemsCatchBlock() throws AuthenticationFailException {
//        // Mock a User and a CartDto
//        User user = new User();
//        CartDto cartDto = new CartDto(); // You can create an empty or sample CartDto
//
//        // Mock helper.getUserFromToken to return the user
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock cartService.listCartItems to throw a simulated exception
//        doAnswer(invocation -> {
//            throw new Exception("Simulated Exception");
//        }).when(cartService).listCartItems(user);
//
//        // Call the getCartItems method and capture the response
//        ResponseEntity<CartDto> response = cartController.getCartItems(new MockHttpServletRequest());
//
//        // Assert that the response status code is INTERNAL_SERVER_ERROR
//        // and the response body is an empty CartDto (or matches the expected content)
//        ResponseEntity<CartDto> expectedResponse = new ResponseEntity<>(new CartDto(), HttpStatus.INTERNAL_SERVER_ERROR);
//        Assertions.assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("Delete Cart items")
//    void testDeleteCartItem() throws AuthenticationFailException {
//        long productId = 1L; // valid product ID
//        User user = new User();
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of cartService.deleteCartItem
//        doNothing().when(cartService).deleteCartItem(productId, user);
//
//        // Call the API method
//        ResponseEntity<ApiResponse> responseEntity = cartController.deleteCartItem(productId, mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Item has been removed from cart successfully", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//
//        // Verify that cartService.deleteCartItem was called with the correct arguments
//        verify(cartService).deleteCartItem(productId, user);
//    }
//
//    @Test
//    void testDeleteCartItemCatchBlock() throws AuthenticationFailException {
//        // Set a sample product ID
//        Long productId = 1L;
//        User user = new User();
//
//        // Mock the helper.getUserFromToken to return a User
//       when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock cartService.deleteCartItem to throw a simulated exception
//        doAnswer(invocation -> {
//            throw new Exception("Simulated Exception");
//        }).when(cartService).deleteCartItem(productId, user);
//
//        // Call the deleteCartItem method and capture the response
//        ResponseEntity<ApiResponse> response = cartController.deleteCartItem(productId, new MockHttpServletRequest());
//
//        // Assert that the response status code is INTERNAL_SERVER_ERROR
//        // and the message in the ApiResponse is as expected
//        ResponseEntity<ApiResponse> expectedResponse = new ResponseEntity<>(new ApiResponse(false, "Failed to delete cart item"), HttpStatus.INTERNAL_SERVER_ERROR);
//        Assertions.assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
//    }
//
//
//    @Test
//    @DisplayName("Update Cart Item")
//    void testUpdateCartItem() throws AuthenticationFailException {
//        AddToCartDto addToCartDto = new AddToCartDto(); // Replace with a valid DTO
//        User user = new User(); // Replace with a valid user
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of cartService.updateCartItem
//        doNothing().when(cartService).updateCartItem(addToCartDto, user);
//
//        // Call the API method
//        ResponseEntity<ApiResponse> responseEntity = cartController.updateCartItem(addToCartDto, mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Cart item has been updated", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//
//        // Verify that cartService.updateCartItem was called with the correct arguments
//        verify(cartService).updateCartItem(addToCartDto, user);
//    }
//
//    @Test
//    void testUpdateCartItemCatchBlock() throws AuthenticationFailException {
//        // Create a sample AddToCartDto
//        AddToCartDto addToCartDto = new AddToCartDto();
//        User user = new User();
//
//        // Mock the helper.getUserFromToken to return a User
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock cartService.updateCartItem to throw a simulated exception
//        doAnswer(invocation -> {
//            throw new Exception("Simulated Exception");
//        }).when(cartService).updateCartItem(addToCartDto,user);
//
//        // Call the updateCartItem method and capture the response
//        ResponseEntity<ApiResponse> response = cartController.updateCartItem(addToCartDto, new MockHttpServletRequest());
//
//        // Assert that the response status code is INTERNAL_SERVER_ERROR
//        // and the message in the ApiResponse is as expected
//        ResponseEntity<ApiResponse> expectedResponse = new ResponseEntity<>(new ApiResponse(false, "Failed to update cart item"), HttpStatus.INTERNAL_SERVER_ERROR);
//        Assertions.assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
//    }
//
//
//    @Test
//    @DisplayName("Update Quantity")
//    void testUpdateQuantity() throws AuthenticationFailException {
//        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto(); // Replace with a valid DTO
//        User user = new User(); // Replace with a valid user
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of cartService.updateProductQuantity
//        doNothing().when(cartService).updateProductQuantity(updateProductQuantityDto, user);
//
//        // Call the API method
//        ResponseEntity<ApiResponse> responseEntity = cartController.updateQuantity(updateProductQuantityDto, mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals("Product quantity has been updated successfully", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//
//        // Verify that cartService.updateProductQuantity was called with the correct arguments
//        verify(cartService).updateProductQuantity(updateProductQuantityDto, user);
//    }
//
//    @Test
//    void testUpdateQuantityCatchBlock() throws AuthenticationFailException {
//        // Create a sample UpdateProductQuantityDto
//        UpdateProductQuantityDto updateProductQuantityDto = new UpdateProductQuantityDto();
//        updateProductQuantityDto.setProductId(1L); // Set a product ID
//        User user = new User();
//
//        // Mock the helper.getUserFromToken to return a User
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock cartService.updateProductQuantity to throw a simulated exception
//        doAnswer(invocation -> {
//            throw new Exception("Simulated Exception");
//        }).when(cartService).updateProductQuantity(updateProductQuantityDto, user);
//
//        // Call the updateQuantity method and capture the response
//        ResponseEntity<ApiResponse> response = cartController.updateQuantity(updateProductQuantityDto, new MockHttpServletRequest());
//
//        // Assert that the response status code is INTERNAL_SERVER_ERROR
//        // and the message in the ApiResponse is as expected
//        ResponseEntity<ApiResponse> expectedResponse = new ResponseEntity<>(new ApiResponse(false, "Failed to update product quantity in cart"), HttpStatus.INTERNAL_SERVER_ERROR);
//        Assertions.assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
//    }
//
//}