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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;

import static com.nineleaps.leaps.config.MessageStrings.ORDER_ITEM_UNAUTHORIZED_ACCESS;

@RestController
@RequestMapping("/api/v1/order")
@AllArgsConstructor
@Slf4j
@Validated
@Api(tags = "Order Api")
public class OrderController {

    //Linking layers using constructor injection

    private final OrderServiceInterface orderService;
    private final Helper helper;


    //API : To place order after checkout

    @ApiOperation(value = "API : Add new order after successful payment")
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")

    public ResponseEntity<ApiResponse> placeOrder(@RequestParam("razorpayId") String razorpayId, HttpServletRequest request) throws AuthenticationFailException {

        // Guard Statement : The required parameter razorpayId is missing or empty

        if (razorpayId == null || razorpayId.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "Missing or empty razorpayId parameter"), HttpStatus.BAD_REQUEST);
        }

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        // Calling service layer to place order

        orderService.placeOrder(user, razorpayId);
        return new ResponseEntity<>(new ApiResponse(true, "Order has been placed"), HttpStatus.CREATED);
    }

    //API : To get list of order for particular user

    @ApiOperation(value = "API : To List all the orders for a particular user")
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")

    public ResponseEntity<List<OrderDto>> getAllOrders(HttpServletRequest request) throws AuthenticationFailException {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        // Calling service layer to get orders

        List<OrderDto> orders = orderService.listOrders(user);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //API : To get order items for an order

    @ApiOperation(value = "API : Get details of an order")
    @GetMapping(value = "/getOrderById/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")

    public ResponseEntity<Order> getOrderById(@PathVariable("orderId") Long orderId, HttpServletRequest request) throws AuthenticationFailException {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        //Calling service layer to get order items

        Order order = orderService.getOrder(orderId, user);
        return new ResponseEntity<>(order, HttpStatus.OK);

    }

    //API : To alter order status i.e. transit, delivered, pickup and return

    @ApiOperation(value = "API : To alter order status")
    @PostMapping(value = "/order-status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")

    // Validating the orderStatus variable

    public ResponseEntity<ApiResponse> orderInTransit(@RequestParam("orderItemId") Long orderItemId, @NonNull @RequestParam("Order Status") String orderStatus, HttpServletRequest request) throws AuthenticationFailException {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        //Guard Statement : Check if order item belongs to the current user

        OrderItem orderItem = orderService.getOrderItem(orderItemId, user);
        if (!Helper.notNull(orderItem)) {
            return new ResponseEntity<>(new ApiResponse(false, ORDER_ITEM_UNAUTHORIZED_ACCESS), HttpStatus.FORBIDDEN);
        }

        orderService.orderStatus(orderItem, orderStatus); // IN TRANSIT, DELIVERED, PICKED UP, RETURNED

        return new ResponseEntity<>(new ApiResponse(true, "Order is " + orderStatus), HttpStatus.CREATED);
    }

    //API : To get owner order received history or rented products

    @ApiOperation(value = "API : To get owner order received history")
    @GetMapping(value = "/owner-order-history", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    public ResponseEntity<List<ProductDto>> getRentedOutProducts(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize, HttpServletRequest request) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        //Calling service layer to get owner order received history

        List<ProductDto> body = orderService.getRentedOutProducts(user, pageNumber, pageSize);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //API : To get orders by shipping/order status

    @ApiOperation(value = "API : To get orders by shipping/order status")
    @GetMapping(value = "/shipping-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    // Validating shipping status

    public ResponseEntity<List<OrderItemDto>> getShippingStatus(@NotBlank @RequestParam("status") String shippingStatus, HttpServletRequest request) {

        // JWT : Extracting user info from token

        User user = helper.getUser(request);

        //Calling service layer to get orders by shipping/order status

        List<OrderItemDto> body = orderService.getOrdersItemByStatus(shippingStatus, user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    //API : To get generate invoice

    @ApiOperation(value = "API : To get generate invoice")
    @GetMapping(value = "/generateInvoice/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('BORROWER')")

    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId, HttpServletRequest request) throws OrderNotFoundException {
        try {

            // JWT : Extracting user info from token

            User user = helper.getUser(request);

            //Guard Statement : Check if order item belongs to the current user

            Order order = orderService.getOrder(orderId, user);
            if(!Helper.notNull(order)) {
                throw new OrderNotFoundException("No order items found for the user and order ID");
            }

            // Retrieve order items associated with the order from entity relation

            List<OrderItem> orderItems = order.getOrderItems();

            // Generate the PDF invoice

            byte[] pdfBytes = orderService.generateInvoicePDF(orderItems,user,order);

            // Set headers for the PDF response

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice.pdf");

            // Return the PDF bytes as a ResponseEntity

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException | DocumentException e) {
            log.error(String.valueOf(e)); // Handle the exception appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

