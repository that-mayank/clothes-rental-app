package com.nineleaps.leaps.controller;

import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Tag("unit")
class OrderControllerTest {

    @Mock
    private OrderServiceInterface orderService;
    @Mock
    private Helper helper;
    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Place Order - Success")
    void placeOrder_ReturnsApiResponse()  {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        String razorpayId = "razorpay123";

        when(helper.getUser(request)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.placeOrder(razorpayId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Order has been placed", response.getMessage());

        // Verify that the service method was called with the correct arguments
        verify(orderService).placeOrder(request, razorpayId);
    }

    @Test
    @DisplayName("Place Order - Missing Razorpay Id")
    void placeOrder_MissingRazorpayId_ReturnsBadRequest()  {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.placeOrder(null, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Missing or empty razorpayId parameter", response.getMessage());

        // Verify that the service method was not called
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @DisplayName("Place Order - Empty Razorpay Id")
    void placeOrder_EmptyRazorpayId_ReturnsBadRequest()  {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.placeOrder("", request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Missing or empty razorpayId parameter", response.getMessage());

        // Verify that the service method was not called
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @DisplayName("Get All Orders - Success")
    void getAllOrders_ReturnsOrderDtoList()  {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<OrderDto> orderDtoList = Collections.singletonList(new OrderDto());

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.listOrders(request)).thenReturn(orderDtoList);

        // Act
        ResponseEntity<List<OrderDto>> responseEntity = orderController.getAllOrders(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<OrderDto> resultOrderDtoList = responseEntity.getBody();
        assertNotNull(resultOrderDtoList);
        assertEquals(orderDtoList, resultOrderDtoList);

        // Verify that the service method was called with the correct arguments
        verify(orderService).listOrders(request);
    }

    @Test
    @DisplayName("Get Order - Success")
    void getOrderById_ReturnsOrder() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderId = 1L;
        Order order = new Order();

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrder(orderId, request)).thenReturn(order);

        // Act
        ResponseEntity<Order> responseEntity = orderController.getOrderById(orderId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Order resultOrder = responseEntity.getBody();
        assertNotNull(resultOrder);
        assertEquals(order, resultOrder);

        // Verify that the service method was called with the correct arguments
        verify(orderService).getOrder(orderId, request);
    }

    @Test
    @DisplayName("Order Status - Success")
    void orderInTransit_ReturnsApiResponse()  {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderItemId = 1L;
        String orderStatus = "IN TRANSIT";

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrderItem(orderItemId, user)).thenReturn(new OrderItem());

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.orderStatus(orderItemId, orderStatus, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Order is " + orderStatus, response.getMessage());
    }

    @Test
    @DisplayName("Order Status - Unauthorized Access")
    void orderInTransit_UnauthorizedAccess_ReturnsForbidden()  {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderItemId = 1L;
        String orderStatus = "IN TRANSIT";

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrderItem(orderItemId, user)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.orderStatus(orderItemId, orderStatus, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("OrderItem does not belong to current user", response.getMessage());

    }

    @Test
    @DisplayName("Order Status - Invalid OrderItem")
    void testOrderInTransitOrderItemIdNull() {
        // Prepare request parameters with null orderItemId
        String orderStatus = "IN TRANSIT";

        // Prepare a mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Call the orderInTransit method
        ResponseEntity<ApiResponse> response = orderController.orderStatus(null, orderStatus, request);


        // Check if the response is as expected
        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()),
                () -> assertFalse(Objects.requireNonNull(response.getBody()).isSuccess()),
                () -> assertNotNull(Objects.requireNonNull(response.getBody()).getMessage()),
                () -> assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("OrderItem does not belong to current user"))
        );
    }

    @Test
    @DisplayName("Order Status - Blank Order Status Variable")
    void testOrderInTransitOrderStatusBlank() {
        // Prepare request parameters with blank orderStatus
        Long orderItemId = 123L;
        String orderStatus = "   ";  // Blank orderStatus

        // Prepare a mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Call the orderInTransit method
        ResponseEntity<ApiResponse> response = orderController.orderStatus(orderItemId, orderStatus, request);


        // Check if the response is as expected
        assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()),
                () -> assertFalse(Objects.requireNonNull(response.getBody()).isSuccess()),
                () -> assertNotNull(Objects.requireNonNull(response.getBody()).getMessage())
        );
    }

    @Test
    @DisplayName("Get Rented Products - Success")
    void getRentedOutProducts_ReturnsProductDtoList() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<ProductDto> productDtoList = Collections.singletonList(new ProductDto());

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getRentedOutProducts(request, 0, 100)).thenReturn(productDtoList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = orderController.getRentedOutProducts(0, 100, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> resultProductDtoList = responseEntity.getBody();
        assertNotNull(resultProductDtoList);
        assertEquals(productDtoList, resultProductDtoList);

        // Verify that the service method was called with the correct arguments
        verify(orderService).getRentedOutProducts(request, 0, 100);
    }

    @Test
    @DisplayName("Get Shipping Status - Success")
    void getShippingStatus_ReturnsOrderItemDtoList() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        String shippingStatus = "SHIPPED";
        List<OrderItemDto> orderItemDtoList = Collections.singletonList(new OrderItemDto());

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrdersItemByStatus(shippingStatus, request)).thenReturn(orderItemDtoList);

        // Act
        ResponseEntity<List<OrderItemDto>> responseEntity = orderController.getShippingStatus(shippingStatus, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<OrderItemDto> resultOrderItemDtoList = responseEntity.getBody();
        assertNotNull(resultOrderItemDtoList);
        assertEquals(orderItemDtoList, resultOrderItemDtoList);

        // Verify that the service method was called with the correct arguments
        verify(orderService).getOrdersItemByStatus(shippingStatus, request);
    }

    @Test
    @DisplayName("Generate Invoice")
    void generateInvoice_ReturnsPdfBytes() throws DocumentException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderId = 1L;
        Order order = new Order();
        byte[] pdfBytes = new byte[1024]; // Mock PDF bytes

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrder(orderId, request)).thenReturn(order);
        when(orderService.generateInvoicePDF(anyList(), eq(user), eq(order))).thenReturn(pdfBytes);

        // Act
        ResponseEntity<byte[]> responseEntity = orderController.generateInvoice(orderId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        byte[] resultPdfBytes = responseEntity.getBody();
        assertNotNull(resultPdfBytes);
        assertArrayEquals(pdfBytes, resultPdfBytes);

        // Verify that the necessary methods were called
        verify(orderService, times(1)).getOrder(orderId, request);
        verify(orderService, times(1)).generateInvoicePDF(anyList(), eq(user), eq(order));
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @DisplayName("Generate Invoice - Exception")
    void generateInvoice_ExceptionDuringPdfGeneration_ReturnsInternalServerError() throws DocumentException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderId = 1L;
        Order order = new Order();

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrder(orderId, request)).thenReturn(order);
        when(orderService.generateInvoicePDF(anyList(), eq(user), eq(order)))
                .thenThrow(new DocumentException("PDF generation failed"));

        // Act
        ResponseEntity<byte[]> responseEntity = orderController.generateInvoice(orderId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Generate Invoice - Null Order")
    void testGenerateInvoiceOrderIsNull() {
        // Prepare request parameters
        Long orderId = 123L;

        // Prepare a mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Prepare a mock User
        User user = new User();
        user.setId(1L);

        // Mock the behavior of helper.getUser(request)
        when(helper.getUser(request)).thenReturn(user);

        // Mock the behavior of orderService.getOrder
        when(orderService.getOrder(orderId, request)).thenReturn(null);

        // Call the generateInvoice method and expect an exception
        assertThrows(OrderNotFoundException.class, () -> orderController.generateInvoice(orderId, request), "Expected OrderNotFoundException was not thrown");
    }
}