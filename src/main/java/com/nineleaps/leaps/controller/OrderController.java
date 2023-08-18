package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.orders.OrderDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.nineleaps.leaps.config.MessageStrings.ORDER_ITEM_UNAUTHORIZED_ACCESS;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/order")
@AllArgsConstructor
@Api(tags = "Order Api", description = "Contains api for adding order, listing order, get particular order details and dashboard api")
@SuppressWarnings("deprecation")
public class OrderController {
    private final OrderServiceInterface orderService;
    private final Helper helper;


    //place order after checkout
    @ApiOperation(value = "Add new order after successful payment")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam("razorpayId") String razorpayId, HttpServletRequest request) throws AuthenticationFailException {
        //authenticate the token
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //place the order
        orderService.placeOrder(user, razorpayId);
        return new ResponseEntity<>(new ApiResponse(true, "Order has been placed"), HttpStatus.CREATED);
    }

    //get all orders
    @ApiOperation(value = "List all the orders for a particular user")
    @GetMapping("/list")
    public ResponseEntity<List<OrderDto>> getAllOrders(HttpServletRequest request) throws AuthenticationFailException {
        //authenticate token
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //get orders
        List<OrderDto> orders = orderService.listOrders(user);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //get order items for an order
    @ApiOperation(value = "Get details of an order")
    @GetMapping("/getOrderById/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable("orderId") Long orderId, HttpServletRequest request) throws AuthenticationFailException {
        //authenticate token
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //check if the order belong to current user
        Order order = orderService.getOrder(orderId, user);
        return new ResponseEntity<>(order, HttpStatus.OK);

    }

    //Dummy  apis for transit, delivered, pickup and return
    @PostMapping("/orderInTransit")
    public ResponseEntity<ApiResponse> orderInTransit(@RequestParam("orderItemId") Long orderItemId, HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //check if order item belong to current user
        OrderItem orderItem = orderService.getOrderItem(orderItemId, user);
        if (!Helper.notNull(orderItem)) {
            return new ResponseEntity<>(new ApiResponse(false, ORDER_ITEM_UNAUTHORIZED_ACCESS), HttpStatus.FORBIDDEN);
        }
        orderService.orderStatus(orderItem, "IN TRANSIT");
        return new ResponseEntity<>(new ApiResponse(true, "Order is in transit"), HttpStatus.OK);
    }

    @PostMapping("/orderDelivered")
    public ResponseEntity<ApiResponse> orderDelivered(@RequestParam("orderItemId") Long orderItemId, HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //check if order belong to current user
        OrderItem orderItem = orderService.getOrderItem(orderItemId, user);
        if (!Helper.notNull(orderItem)) {
            return new ResponseEntity<>(new ApiResponse(false, ORDER_ITEM_UNAUTHORIZED_ACCESS), HttpStatus.FORBIDDEN);
        }
        orderService.orderStatus(orderItem, "DELIVERED");
        return new ResponseEntity<>(new ApiResponse(true, "Order delivered"), HttpStatus.OK);
    }

    @PostMapping("/orderPickup")
    public ResponseEntity<ApiResponse> orderPickup(@RequestParam("orderItemId") Long orderItemId, HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //check if order belong to current user
        OrderItem orderItem = orderService.getOrderItem(orderItemId, user);
        if (!Helper.notNull(orderItem)) {
            return new ResponseEntity<>(new ApiResponse(false, ORDER_ITEM_UNAUTHORIZED_ACCESS), HttpStatus.FORBIDDEN);
        }
        orderService.orderStatus(orderItem, "PICKED UP");
        return new ResponseEntity<>(new ApiResponse(true, "Order is picked up"), HttpStatus.OK);
    }

    @PostMapping("/orderReturned")
    public ResponseEntity<ApiResponse> orderReturned(@RequestParam("orderItemId") Long orderItemId, HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //check if order belong to current user
        OrderItem orderItem = orderService.getOrderItem(orderItemId, user);
        if (!Helper.notNull(orderItem)) {
            return new ResponseEntity<>(new ApiResponse(false, "Order does not belong to current user"), HttpStatus.FORBIDDEN);
        }
        orderService.orderStatus(orderItem, "ORDER RETURNED");
        return new ResponseEntity<>(new ApiResponse(true, "Order is returned"), HttpStatus.OK);
    }


    @GetMapping("/owner-order-history") //rentedProducts
    public ResponseEntity<List<ProductDto>> getRentedOutProducts(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        List<ProductDto> body = orderService.getRentedOutProducts(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}

