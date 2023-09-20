package com.nineleaps.leaps.controller;

import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static com.nineleaps.leaps.config.MessageStrings.ORDER_ITEM_UNAUTHORIZED_ACCESS;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/order")
@Slf4j
@Api(tags = "Order Api", description = "Contains API for adding orders, listing orders, getting particular order details, and dashboard API")
@SuppressWarnings("deprecation")
public class OrderController {

    /**
     * Status Code: 200 - HttpStatus.OK
     * Description: The request was successful, and the response contains the requested data.

     * Status Code: 201 - HttpStatus.CREATED
     * Description: The request was successful, and a new resource has been created as a result.

     * Status Code: 500 - HttpStatus.INTERNAL_SERVER_ERROR
     * Description: An error occurred on the server and no more specific message is suitable.
     */


    // Order service for handling order-related operations
    private final OrderServiceInterface orderService;
    private final Helper helper;



    // API to place a new order after successful payment
    @ApiOperation(value = "Add a new order after successful payment")
    @PostMapping(value = "/add",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam("razorpayId") String razorpayId, HttpServletRequest request) throws AuthenticationFailException {

        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Place the order
        orderService.placeOrder(user, razorpayId);

        // Return success response
        return new ResponseEntity<>(new ApiResponse(true, "Order has been placed"), HttpStatus.CREATED);
    }


    // API to get all orders for a particular user
    @ApiOperation(value = "List all the orders for a particular user")
    @GetMapping(value = "/list",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<OrderDto>> getAllOrders(HttpServletRequest request) throws AuthenticationFailException {

        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Get orders for the user
        List<OrderDto> orders = orderService.listOrders(user);

        // Return the list of orders
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }



    // API to get order items for a specific order
    @ApiOperation(value = "Get details of an order")
    @GetMapping(value = "/getOrderById/{orderId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<Order> getOrderById(@PathVariable("orderId") Long orderId, HttpServletRequest request) throws AuthenticationFailException {

        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Check if the order belongs to the current user
        Order order = orderService.getOrder(orderId, user);

        // Return the order details
        return new ResponseEntity<>(order, HttpStatus.OK);
    }



    // Test double APIs for order status updates (transit, delivered, pickup, return)
    @PostMapping(value = "/order-status",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> orderInTransit(@RequestParam("orderItemId") Long orderItemId, @NonNull @RequestParam("Order Status") String orderStatus, HttpServletRequest request) throws AuthenticationFailException {

        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Check if the order item belongs to the current user
        OrderItem orderItem = orderService.getOrderItem(orderItemId, user);

        if (!Helper.notNull(orderItem)) {
            return new ResponseEntity<>(new ApiResponse(false, ORDER_ITEM_UNAUTHORIZED_ACCESS), HttpStatus.FORBIDDEN);
        }

        // Update the order status (IN TRANSIT, DELIVERED, PICKED UP, RETURNED)
        orderService.orderStatus(orderItem, orderStatus);

        // Return success response
        return new ResponseEntity<>(new ApiResponse(true, "Order is " + orderStatus), HttpStatus.OK);
    }



    // API to get products rented out by the owner
    @GetMapping(value = "/owner-order-history",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<ProductDto>> getRentedOutProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "1000", required = false) int pageSize, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Get rented out products for the owner
        List<ProductDto> body = orderService.getRentedOutProducts(user, pageNumber, pageSize);

        // Return the list of rented-out products
        return new ResponseEntity<>(body, HttpStatus.OK);
    }



    // API to get order items by shipping status
    @GetMapping(value = "/shipping-status",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<OrderItemDto>> getShippingStatus(@RequestParam("status") String shippingStatus, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Get order items by the specified shipping status
        List<OrderItemDto> body = orderService.getOrdersItemByStatus(shippingStatus, user);

        // Return the list of order items by shipping status
        return new ResponseEntity<>(body, HttpStatus.OK);
    }



    // API to generate an invoice for a specific order
    @GetMapping(value = "/generateInvoice/{orderId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            // Extract User from the token
            User user = helper.getUserFromToken(request);

            // Get the order and order items
            Order order = orderService.getOrder(orderId, user);
            List<OrderItem> orderItems = order.getOrderItems();

            // Generate the invoice in PDF format
            byte[] pdfBytes = orderService.generateInvoicePDF(orderItems, user, order);

            // set the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice.pdf");

            // Return the generated invoice as a byte array
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException | DocumentException e) {
            log.error(String.valueOf(e)); // Handle the exception appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}


