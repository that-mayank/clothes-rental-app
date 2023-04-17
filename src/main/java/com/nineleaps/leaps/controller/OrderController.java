package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.checkout.CheckoutItemDto;
import com.nineleaps.leaps.dto.checkout.StripeResponse;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.service.AuthenticationServiceInterface;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final AuthenticationServiceInterface authenticationService;
    private final OrderServiceInterface orderService;

    public OrderController(AuthenticationServiceInterface authenticationService, OrderServiceInterface orderService) {
        this.authenticationService = authenticationService;
        this.orderService = orderService;
    }

    //stripe create session api
    @PostMapping("/create-checkout-session")
    public ResponseEntity<StripeResponse> checkoutList(@RequestBody List<CheckoutItemDto> checkoutItemDtoList) throws StripeException {
        //create stripe session
        Session session = orderService.createSession(checkoutItemDtoList);
        StripeResponse stripeResponse = new StripeResponse(session.getId());
        //send the stripe session id in response
        return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
    }

    //place order after checkout
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam("token") String token, @RequestParam("sessionId") String sessionId) throws AuthenticationFailException {
        //authenticate the token
        authenticationService.authenticate(token);
        //retrieve user
        User user = authenticationService.getUser(token);
        //place the order
        orderService.placeOrder(user, sessionId);
        return new ResponseEntity<>(new ApiResponse(true, "Order has been placed"), HttpStatus.CREATED);
    }

    //get all orders
    @GetMapping("/list")
    public ResponseEntity<List<Order>> getAllOrders(@RequestParam("token") String token, @RequestParam("sessionId") String sessionId) throws AuthenticationFailException {
        //authenticate token
        authenticationService.authenticate(token);
        //retrieve user
        User user = authenticationService.getUser(token);
        //get orders
        List<Order> orders = orderService.listOrders(user);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //get orderitems for an order
    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(@PathVariable("orderId") Long orderId, @RequestParam("token") String token) throws AuthenticationFailException {
        //authenticate token
        authenticationService.authenticate(token);
        //get user
        User user = authenticationService.getUser(token);
        //check if the order belong to current user
        Order order = orderService.getOrder(orderId, user);
//        Order order = orderService.getOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);

    }
}
