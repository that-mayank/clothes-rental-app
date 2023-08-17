package com.nineleaps.leaps.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

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



    @ApiOperation(value = "Gives details about how many orders the owner has got")
    @GetMapping("/onClickDashboard")
    public ResponseEntity<Map<YearMonth, Map<String, Object>>> onClickDashboard(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, Map<String, Object>> body = orderService.onClickDasboard(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ApiOperation(value = "Gives details about how many orders the owner has got")
    @GetMapping("/onClickDashboardYearlyWiseData")
    public ResponseEntity<Map<Year, Map<YearMonth, Map<String, Object>>>> onClickDashboardYearWiseData(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<Year, Map<YearMonth, Map<String, Object>>> body = orderService.onClickDashboardYearWiseData(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/dashboardOrderItems")
    public ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> getOrderItemsDashboard(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, List<OrderReceivedDto>> body = orderService.getOrderedItemsByMonth(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/dashboardSubCategoriesAnalytics")
    public ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> getOrderItemsBySubCategories(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, Map<String, OrderItemsData>> body = orderService.getOrderItemsBySubCategories(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/dashboardCategoriesAnalytics")
    public ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> getOrderItemsByCategories(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, Map<String, OrderItemsData>> body = orderService.getOrderItemsByCategories(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/rentedProducts")
    public ResponseEntity<List<ProductDto>> getRentedOutProducts(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        List<ProductDto> body = orderService.getRentedOutProducts(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    @GetMapping("/exportPdf")
    public ResponseEntity<InputStreamResource> getPdf(HttpServletRequest request) throws IOException, DocumentException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        Document document = orderService.getPdf(user);

        // Convert the Document into a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        // Open the document
        document.open();

        // Add content to the document
        orderService.addContent(document, user);

        // Close the document
        document.close();

        byte[] pdfBytes = baos.toByteArray();

        // Create the InputStreamResource from the byte array
        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));

        // Set the Content-Disposition header to force download the PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "report.pdf");

            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }

    @GetMapping("/dashboardDateSelector")
    public ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> getOrderItemsDashboardBwDates(HttpServletRequest request, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, List<OrderReceivedDto>> body = orderService.getOrderedItemsByMonthBwDates(user, startDate, endDate);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}

