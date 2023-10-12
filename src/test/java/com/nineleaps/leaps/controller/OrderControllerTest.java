package com.nineleaps.leaps.controller;

import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    void placeOrder_ReturnsApiResponse() throws AuthenticationFailException {
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
        verify(orderService).placeOrder(user, razorpayId);
    }

    @Test
    void placeOrder_MissingRazorpayId_ReturnsBadRequest() throws AuthenticationFailException {
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
        verifyZeroInteractions(orderService);
    }

    @Test
    void getAllOrders_ReturnsOrderDtoList() throws AuthenticationFailException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<OrderDto> orderDtoList = Collections.singletonList(new OrderDto());

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.listOrders(user)).thenReturn(orderDtoList);

        // Act
        ResponseEntity<List<OrderDto>> responseEntity = orderController.getAllOrders(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<OrderDto> resultOrderDtoList = responseEntity.getBody();
        assertNotNull(resultOrderDtoList);
        assertEquals(orderDtoList, resultOrderDtoList);

        // Verify that the service method was called with the correct arguments
        verify(orderService).listOrders(user);
    }

    @Test
    void getOrderById_ReturnsOrder() throws AuthenticationFailException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderId = 1L;
        Order order = new Order();

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrder(orderId, user)).thenReturn(order);

        // Act
        ResponseEntity<Order> responseEntity = orderController.getOrderById(orderId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Order resultOrder = responseEntity.getBody();
        assertNotNull(resultOrder);
        assertEquals(order, resultOrder);

        // Verify that the service method was called with the correct arguments
        verify(orderService).getOrder(orderId, user);
    }

    @Test
    void orderInTransit_ReturnsApiResponse() throws AuthenticationFailException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderItemId = 1L;
        String orderStatus = "IN TRANSIT";

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrderItem(orderItemId, user)).thenReturn(new OrderItem());

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.orderInTransit(orderItemId, orderStatus, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Order is " + orderStatus, response.getMessage());

        // Verify that the service method was called with the correct arguments
        verify(orderService).orderStatus(any(OrderItem.class), eq(orderStatus));
    }

    @Test
    void orderInTransit_UnauthorizedAccess_ReturnsForbidden() throws AuthenticationFailException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderItemId = 1L;
        String orderStatus = "IN TRANSIT";

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrderItem(orderItemId, user)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.orderInTransit(orderItemId, orderStatus, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("OrderItem does not belong to current user", response.getMessage());

    }

    @Test
    void getRentedOutProducts_ReturnsProductDtoList() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<ProductDto> productDtoList = Collections.singletonList(new ProductDto());

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getRentedOutProducts(user, 0, 100)).thenReturn(productDtoList);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = orderController.getRentedOutProducts(0, 100, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> resultProductDtoList = responseEntity.getBody();
        assertNotNull(resultProductDtoList);
        assertEquals(productDtoList, resultProductDtoList);

        // Verify that the service method was called with the correct arguments
        verify(orderService).getRentedOutProducts(user, 0, 100);
    }

    @Test
    void getShippingStatus_ReturnsOrderItemDtoList() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        String shippingStatus = "SHIPPED";
        List<OrderItemDto> orderItemDtoList = Collections.singletonList(new OrderItemDto());

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrdersItemByStatus(shippingStatus, user)).thenReturn(orderItemDtoList);

        // Act
        ResponseEntity<List<OrderItemDto>> responseEntity = orderController.getShippingStatus(shippingStatus, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<OrderItemDto> resultOrderItemDtoList = responseEntity.getBody();
        assertNotNull(resultOrderItemDtoList);
        assertEquals(orderItemDtoList, resultOrderItemDtoList);

        // Verify that the service method was called with the correct arguments
        verify(orderService).getOrdersItemByStatus(shippingStatus, user);
    }

    @Test
    void generateInvoice_ReturnsPdfBytes() throws DocumentException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderId = 1L;
        Order order = new Order();
        byte[] pdfBytes = new byte[1024]; // Mock PDF bytes

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrder(orderId, user)).thenReturn(order);
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
        verify(orderService, times(1)).getOrder(orderId, user);
        verify(orderService, times(1)).generateInvoicePDF(anyList(), eq(user), eq(order));
        verifyNoMoreInteractions(orderService);
    }

    @Test
    void generateInvoice_ExceptionDuringPdfGeneration_ReturnsInternalServerError() throws DocumentException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Long orderId = 1L;
        Order order = new Order();

        when(helper.getUser(request)).thenReturn(user);
        when(orderService.getOrder(orderId, user)).thenReturn(order);
        when(orderService.generateInvoicePDF(anyList(), eq(user), eq(order)))
                .thenThrow(new DocumentException("PDF generation failed"));

        // Act
        ResponseEntity<byte[]> responseEntity = orderController.generateInvoice(orderId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}