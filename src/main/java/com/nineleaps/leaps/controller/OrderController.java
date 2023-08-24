package com.nineleaps.leaps.controller;

import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static com.nineleaps.leaps.config.MessageStrings.ORDER_ITEM_UNAUTHORIZED_ACCESS;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/order")
@AllArgsConstructor
@Slf4j
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
    @PostMapping("/order-status")
    public ResponseEntity<ApiResponse> orderInTransit(@RequestParam("orderItemId") Long orderItemId, @NonNull @RequestParam("Order Status") String orderStatus, HttpServletRequest request) throws AuthenticationFailException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        //check if order item belong to current user
        OrderItem orderItem = orderService.getOrderItem(orderItemId, user);
        if (!Helper.notNull(orderItem)) {
            return new ResponseEntity<>(new ApiResponse(false, ORDER_ITEM_UNAUTHORIZED_ACCESS), HttpStatus.FORBIDDEN);
        }
        orderService.orderStatus(orderItem, orderStatus); // IN TRANSIT, DELIVERED, PICKED UP, RETURNED
        return new ResponseEntity<>(new ApiResponse(true, "Order is " + orderStatus), HttpStatus.OK);
    }


    @GetMapping("/owner-order-history") //rentedProducts
    public ResponseEntity<List<ProductDto>> getRentedOutProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "1000", required = false) int pageSize, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        List<ProductDto> body = orderService.getRentedOutProducts(user, pageNumber, pageSize);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/shipping-status")
    public ResponseEntity<List<OrderItemDto>> getShippingStatus(@RequestParam("status") String shippingStatus, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        List<OrderItemDto> body = orderService.getOrdersItemByStatus(shippingStatus, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/generateInvoice/{orderId}")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring(7);
            User user = helper.getUser(token); // Assuming you have a helper method to get the user

            Order order = orderService.getOrder(orderId, user);

            if(!Helper.notNull(order)) {
                throw new OrderNotFoundException("No order items found for the user and order ID");
            }

            List<OrderItem> orderItems = order.getOrderItems(); //From entity relation

            byte[] pdfBytes = orderService.generateInvoicePDF(orderItems,user,order);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException | DocumentException e) {
            log.error(String.valueOf(e)); // Handle the exception appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

