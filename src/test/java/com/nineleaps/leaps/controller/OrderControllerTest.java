//package com.nineleaps.leaps.controller;
//
//import com.itextpdf.text.DocumentException;
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.common.ApiResponse;
//import com.nineleaps.leaps.dto.orders.OrderDto;
//import com.nineleaps.leaps.dto.orders.OrderItemDto;
//import com.nineleaps.leaps.dto.product.ProductDto;
//
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.model.orders.Order;
//import com.nineleaps.leaps.model.orders.OrderItem;
//import com.nineleaps.leaps.service.OrderServiceInterface;
//import com.nineleaps.leaps.utils.Helper;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//@Tag("unit_tests")
//@DisplayName("Test case file for Order Controller ")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class OrderControllerTest {
//
//    @Mock
//    private OrderServiceInterface orderService;
//
//    @Mock
//    private Helper helper;
//
//    @InjectMocks
//    private OrderController orderController;
//    @Mock
//    private HttpServletRequest request;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("Place order")
//    void testPlaceOrder() {
//        User user = new User();
//        String razorpayId = "sampleRazorpayId";
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of orderService.placeOrder
//        doNothing().when(orderService).placeOrder(user, razorpayId);
//
//        // Call the API method
//        ResponseEntity<ApiResponse> responseEntity = orderController.placeOrder(razorpayId, mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//        ApiResponse responseBody = responseEntity.getBody();
//        assert responseBody != null;
//        assertTrue(responseBody.isSuccess());
//        assertEquals("Order has been placed", responseBody.getMessage());
//    }
//
//    @Test
//    @DisplayName("get all orders")
//    void testGetAllOrders() {
//        User user = new User();
//        List<OrderDto> orders = new ArrayList<>(); // Replace with a list of orders
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of orderService.listOrders
//        when(orderService.listOrders(user)).thenReturn(orders);
//
//        // Call the API method
//        ResponseEntity<List<OrderDto>> responseEntity = orderController.getAllOrders(mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(orders, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("Get order by id")
//    void testGetOrderById() {
//        Long orderId = 123L; // Replace with a valid order ID
//        Order order = new Order(); // Replace with a valid order object
//        User user = new User(); // Replace with a valid user
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of orderService.getOrder
//        when(orderService.getOrder(orderId, user)).thenReturn(order);
//
//        // Call the API method
//        ResponseEntity<Order> responseEntity = orderController.getOrderById(orderId, mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(order, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("Get order in Transit")
//    void testOrderInTransit(){
//        Long orderItemId = 123L;
//        String orderStatus = "IN_TRANSIT";
//        User user = new User();
//        OrderItem orderItem = new OrderItem();
//
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//        when(orderService.getOrderItem(orderItemId, user)).thenReturn(orderItem);
//
//        // Mock the behavior of orderService.orderStatus (void method)
//        doNothing().when(orderService).orderStatus(orderItem, orderStatus);
//
//        ResponseEntity<ApiResponse> responseEntity = orderController.orderInTransit(orderItemId, orderStatus, mock(HttpServletRequest.class));
//
//        ApiResponse response = responseEntity.getBody();
//
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        Assertions.assertNotNull(response);
//        assertTrue(response.isSuccess());
//        assertEquals("Order is " + orderStatus, response.getMessage());
//    }
//
//    @Test
//    @DisplayName("Get rented out products")
//    void testGetRentedOutProducts() {
//        // Arrange
//        int pageNumber = 0;
//        int pageSize = 10;
//        User user = new User();
//        List<ProductDto> expectedProducts = Collections.singletonList(new ProductDto());
//
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//        when(orderService.getRentedOutProducts(user, pageNumber, pageSize)).thenReturn(expectedProducts);
//
//        // Act
//        ResponseEntity<List<ProductDto>> responseEntity = orderController.getRentedOutProducts(pageNumber, pageSize, mock(HttpServletRequest.class));
//
//        // Assert
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(expectedProducts, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("Get shipping status")
//    void testGetShippingStatus() {
//        // Arrange
//        String shippingStatus = "SHIPPED";
//        User user = new User();
//        List<OrderItemDto> expectedOrderItems = Collections.singletonList(new OrderItemDto());
//
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//        when(orderService.getOrdersItemByStatus(shippingStatus, user)).thenReturn(expectedOrderItems);
//
//        // Act
//        ResponseEntity<List<OrderItemDto>> responseEntity = orderController.getShippingStatus(shippingStatus, mock(HttpServletRequest.class));
//
//        // Assert
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(expectedOrderItems, responseEntity.getBody());
//    }
//
//    @Test
//    @DisplayName("Generate Invoice")
//    void testGenerateInvoice() throws IOException, DocumentException {
//        // Arrange
//        Long orderId = 123L;
//        User user = new User();
//        Order order = new Order();
//        order.setOrderItems(Collections.singletonList(new OrderItem()));
//        byte[] pdfBytes = "Mock PDF Content".getBytes();
//
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//        when(orderService.getOrder(orderId, user)).thenReturn(order);
//        when(orderService.generateInvoicePDF(order.getOrderItems(), user, order)).thenReturn(pdfBytes);
//
//        // Act
//        ResponseEntity<byte[]> responseEntity = orderController.generateInvoice(orderId, mock(HttpServletRequest.class));
//
//        // Assert
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(pdfBytes, responseEntity.getBody());
//        HttpHeaders headers = responseEntity.getHeaders();
//        assertEquals(MediaType.APPLICATION_PDF, headers.getContentType());
//        assertEquals("form-data; name=\"attachment\"; filename=\"invoice.pdf\"", headers.getContentDisposition().toString());
//    }
//
//    @Test
//    @DisplayName("Order in transit")
//    void testOrderInTransit_UnauthorizedAccess() {
//        // Mock valid user
//        User user = new User();
//        user.setId(1L);
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock an order item that belongs to the user
//        OrderItem authorizedOrderItem = new OrderItem();
//        authorizedOrderItem.setId(1L); // User's order item ID
//        when(orderService.getOrderItem(eq(1L), any())).thenReturn(authorizedOrderItem);
//
//        // Mock an order item that does not belong to the user
//        OrderItem unauthorizedOrderItem = new OrderItem();
//        unauthorizedOrderItem.setId(999L); // Some other user's order item ID
//        when(orderService.getOrderItem(eq(999L), any())).thenReturn(null);
//
//        // Call the API with an order item that doesn't belong to the user
//        ResponseEntity<ApiResponse> responseEntity = orderController.orderInTransit(999L, "IN_TRANSIT", request);
//
//        // Verify the response
//        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
//        assertEquals("OrderItem does not belong to current user", Objects.requireNonNull(responseEntity.getBody()).getMessage());
//    }
//    @Test
//    @DisplayName("Generate invoice - Causes Internal server error")
//    void testGenerateInvoice_InternalServerError() throws IOException, DocumentException {
//        // Mock valid user
//        User user = new User();
//        when(helper.getUserFromToken(any())).thenReturn(user);
//
//        // Mock order and order items
//        Order order = new Order();
//        order.setId(1L);
//        when(orderService.getOrder(eq(1L), any())).thenReturn(order);
//
//        // Mock an IOException during invoice generation
//        when(orderService.generateInvoicePDF(any(), any(), any())).thenThrow(new IOException());
//
//        // Call the API to generate an invoice
//        ResponseEntity<byte[]> responseEntity = orderController.generateInvoice(1L, null);
//
//        // Verify the response
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//
//    @Test
//     void testPlaceOrderCatchBlock() {
//        // Mock HttpServletRequest and its methods
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        // Mock user
//        User user = new User();
//        user.setEmail("test@example.com");
//
//        // Mock helper to return the user
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock orderService to throw a simulated exception using doAnswer
//        doAnswer(invocation -> {
//            throw new Exception("Simulated exception");
//        }).when(orderService).placeOrder(user, "mockedRazorpayId");
//
//        // Call the method and capture the response
//        ResponseEntity<ApiResponse> responseEntity = orderController.placeOrder("mockedRazorpayId", request);
//
//        // Verify that the response and exception handling are as expected
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//        ApiResponse responseBody = responseEntity.getBody();
//        assert responseBody != null;
//        assertFalse(responseBody.isSuccess());
//        assertEquals("Error placing order", responseBody.getMessage());
//    }
//
//    @Test
//     void testGetAllOrdersCatchBlock() {
//        // Mock HttpServletRequest and its methods
//        HttpServletRequest request =mock(HttpServletRequest.class);
//
//        // Mock user
//        User user = new User();
//        user.setEmail("test@example.com");
//
//        // Mock helper to return the user
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock orderService to throw a simulated exception when listOrders is called
//        when(orderService.listOrders(user)).thenThrow(new RuntimeException("Simulated exception"));
//
//        // Call the method and capture the response
//        ResponseEntity<List<OrderDto> > responseEntity = orderController.getAllOrders(request);
//
//        // Verify that the catch block is executed and the response is internal server error
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//        List<OrderDto> responseBody = responseEntity.getBody();
//        assertNull(responseBody);
//    }
//
//    @Test
//     void testGetOrderByIdCatchBlock() {
//        // Mock HttpServletRequest and its methods
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        // Mock user
//        User user = new User();
//        user.setEmail("test@example.com");
//
//        // Mock helper to return the user
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock orderService to throw a simulated exception when getOrder is called
//        when(orderService.getOrder(anyLong(), eq(user)))
//                .thenThrow(new RuntimeException("Simulated exception"));
//
//
//        // Call the method and capture the response
//        ResponseEntity<Order> responseEntity = orderController.getOrderById(1L, request);
//
//        // Verify that the catch block is executed and the response is internal server error
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//        Order responseBody = responseEntity.getBody();
//        assertNull(responseBody);
//    }
//
//    @Test
//     void testOrderInTransitCatchBlock() {
//        // Mock HttpServletRequest and its methods
//        HttpServletRequest request =mock(HttpServletRequest.class);
//
//        // Mock user
//        User user = new User();
//        user.setEmail("test@example.com");
//
//        // Mock helper to return the user
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//        // Mock orderItem
//        OrderItem orderItem = new OrderItem();
//        orderItem.setId(1L);
//
//        // Mock orderService to throw a simulated exception when orderStatus is called
//        when(orderService.getOrderItem(anyLong(), eq(user))).thenReturn(orderItem);
//        doAnswer(invocation -> {
//            throw new Exception("Simulated exception");
//        }).when(orderService).orderStatus(orderItem, "In Transit");
//
//
//        // Call the method and capture the response
//        ResponseEntity<ApiResponse> responseEntity = orderController.orderInTransit(orderItem.getId(), "In Transit", request);
//
//        // Verify that the catch block is executed and the response is internal server error
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//        ApiResponse responseBody = responseEntity.getBody();
//        assert responseBody != null;
//        assertFalse(responseBody.isSuccess());
//        assertEquals("Error updating order status", responseBody.getMessage());
//    }
//
//    @Test
//     void testGetRentedOutProductsCatchBlock() {
//        // Mock HttpServletRequest and its methods
//        HttpServletRequest request =mock(HttpServletRequest.class);
//
//        // Mock user
//        User user = new User();
//        user.setEmail("test@example.com");
//
//        // Mock helper to return the user
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//
//
//        // Mock orderService to throw a simulated exception when getRentedOutProducts is called
//        when(orderService.getRentedOutProducts(user, 0, 10))
//                .thenThrow(new RuntimeException("Simulated exception"));
//        doAnswer(invocation ->{
//            throw new Exception("Simulated Exception");
//        }).when(orderService).getRentedOutProducts(user,0,10);
//
//        // Call the method and capture the response
//        ResponseEntity<List<ProductDto>> responseEntity = orderController.getRentedOutProducts(0, 10, request);
//
//        // Verify that the catch block is executed and the response is internal server error
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//
//    }
//
//    @Test
//     void testGetShippingStatusCatchBlock() {
//        // Mock HttpServletRequest and its methods
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        // Mock user
//        User user = new User();
//        user.setEmail("test@example.com");
//
//        // Mock helper to return the user
//       when(helper.getUserFromToken(request)).thenReturn(user);
//
//
//
//        // Use doAnswer to throw a simulated exception when getOrdersItemByStatus is called
//        doAnswer(invocation -> {
//                throw new RuntimeException("Simulated exception");
//        }).when(orderService).getOrdersItemByStatus("status", user);
//
//        // Call the method and capture the response
//        ResponseEntity<List<OrderItemDto>> responseEntity = orderController.getShippingStatus("status", request);
//
//        // Verify that the catch block is executed and the response is internal server error
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//
//    }
//
//    @Test
//     void testGenerateInvoiceCatchBlock() {
//        // Mock HttpServletRequest and its methods
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        // Mock user
//        User user = new User();
//        user.setEmail("test@example.com");
//
//        // Mock helper to return the user
//        when(helper.getUserFromToken(request)).thenReturn(user);
//
//
//        // Use doAnswer to throw a simulated exception when getOrder is called
//        doAnswer(invocation -> {
//                throw new IOException("Simulated exception");
//        }).when(orderService).getOrder(1L, user);
//
//        // Call the method and capture the response
//        ResponseEntity<byte[]> responseEntity = orderController.generateInvoice(1L, request);
//
//        // Verify that the catch block is executed and the response is internal server error
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//
//    }
//
//}


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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
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
    void placeOrder_ReturnsApiResponse() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        String razorpayId = "razorpay123";

        when(helper.getUserFromToken(request)).thenReturn(user);

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
    @DisplayName("Get All Orders - Success")
    void getAllOrders_ReturnsOrderDtoList()  {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<OrderDto> orderDtoList = Collections.singletonList(new OrderDto());

        when(helper.getUserFromToken(request)).thenReturn(user);
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

        when(helper.getUserFromToken(request)).thenReturn(user);
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

        when(helper.getUserFromToken(request)).thenReturn(user);
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
    @DisplayName("Get Rented Products - Success")
    void getRentedOutProducts_ReturnsProductDtoList() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<ProductDto> productDtoList = Collections.singletonList(new ProductDto());

        when(helper.getUserFromToken(request)).thenReturn(user);
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

        when(helper.getUserFromToken(request)).thenReturn(user);
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

        when(helper.getUserFromToken(request)).thenReturn(user);
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

        when(helper.getUserFromToken(request)).thenReturn(user);
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
        when(helper.getUserFromToken(request)).thenReturn(user);

        // Mock the behavior of orderService.getOrder
        when(orderService.getOrder(orderId, request)).thenReturn(null);

        // Call the generateInvoice method and expect an exception
        assertThrows(OrderNotFoundException.class, () -> orderController.generateInvoice(orderId, request), "Expected OrderNotFoundException was not thrown");
    }
}
