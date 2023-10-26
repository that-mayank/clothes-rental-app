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
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam("razorpayId") String razorpayId, HttpServletRequest request){

        // Extract User from the token
        User user = helper.getUserFromToken(request);
        try {
            log.info("Placing order for User={}", user.getEmail());
            orderService.placeOrder(user, razorpayId);
            return new ResponseEntity<>(new ApiResponse(true, "Order has been placed"), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error placing order for User={}",user.getEmail() , e);
            return new ResponseEntity<>(new ApiResponse(false, "Error placing order"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // API to get all orders for a particular user
    @ApiOperation(value = "List all the orders for a particular user")
    @GetMapping(value = "/list",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<List<OrderDto>> getAllOrders(HttpServletRequest request) {

        // Extract User from the token
        User user = helper.getUserFromToken(request);

        try {
            log.info("Getting all orders for User={}", user.getEmail());
            List<OrderDto> orders = orderService.listOrders(user);
            return new ResponseEntity<>(orders, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error getting orders for User={}",user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // API to get order items for a specific order
    @ApiOperation(value = "Get details of an order")
    @GetMapping(value = "/getOrderById/{orderId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'BORROWER')")
    public ResponseEntity<Order> getOrderById(@PathVariable("orderId") Long orderId, HttpServletRequest request)  {

        // Extract User from the token
        User user = helper.getUserFromToken(request);

        try {
            log.info("Getting order details for User={}", user.getEmail());
            Order order = orderService.getOrder(orderId, user);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting order details for User={}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // Test double APIs for order status updates (transit, delivered, pickup, return)
    @PostMapping(value = "/order-status",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse> orderInTransit(@RequestParam("orderItemId") Long orderItemId, @NonNull @RequestParam("Order Status") String orderStatus, HttpServletRequest request)  {

        // Extract User from the token
        User user = helper.getUserFromToken(request);
        try {
            log.info("Updating order status for User={}", user.getEmail());
            OrderItem orderItem = orderService.getOrderItem(orderItemId, user);
            if (orderItem == null) {
                return new ResponseEntity<>(new ApiResponse(false, ORDER_ITEM_UNAUTHORIZED_ACCESS), HttpStatus.FORBIDDEN);
            }
            orderService.orderStatus(orderItem, orderStatus);
            return new ResponseEntity<>(new ApiResponse(true, "Order is " + orderStatus), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error updating order status for User={}", user.getEmail(), e);
            return new ResponseEntity<>(new ApiResponse(false, "Error updating order status"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // API to get products rented out by the owner
    @GetMapping(value = "/owner-order-history",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<ProductDto>> getRentedOutProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "1000", required = false) int pageSize, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);
        try {
            log.info("Getting rented-out products for User={}", user.getEmail());
            List<ProductDto> body = orderService.getRentedOutProducts(user, pageNumber, pageSize);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting rented-out products for User={}",user.getEmail() , e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // API to get order items by shipping status
    @GetMapping(value = "/shipping-status",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<OrderItemDto>> getShippingStatus(@RequestParam("status") String shippingStatus, HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        try {
            log.info("Getting order items by shipping status for User={}", user.getEmail());
            List<OrderItemDto> body = orderService.getOrdersItemByStatus(shippingStatus, user);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting order items by shipping status for User={}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // API to generate an invoice for a specific order
    @GetMapping(value = "/generateInvoice/{orderId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER','BORROWER')")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId, HttpServletRequest request) {
        User user = helper.getUserFromToken(request);
        try {
            log.info("Generating invoice for User={}", user.getEmail());
            Order order = orderService.getOrder(orderId, user);
            List<OrderItem> orderItems = order.getOrderItems();
            byte[] pdfBytes = orderService.generateInvoicePDF(orderItems, user, order);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException | DocumentException e) {
            log.error("Error generating invoice for User={}",user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





}


