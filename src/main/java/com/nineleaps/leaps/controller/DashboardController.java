package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;




@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    // Linking layers using Constructor Injection
    private final DashboardServiceInterface dashboardService;
    private final OrderServiceInterface orderService;
    private final Helper helper;

    // API - Allows the owner to view his dashboard
    @ApiOperation(value = "Gives details about how many orders the owner has got and the total earnings")
    @GetMapping(value = "/owner-view", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Object>> dashboard(HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Calling the service layer to get dashboard details for the owner
        Map<String, Object> result = dashboardService.dashboardOwnerView(user);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // API - Allows the owner to get details about the total number of the order owner has got
    @ApiOperation(value = "Gives details about how many orders the owner has got")
    @GetMapping(value = "/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, Map<String, Object>>> onClickDashboard(HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Calling the service layer to get all the orders placed for that particular owner
        Map<YearMonth, Map<String, Object>> body = dashboardService.analytics(user);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API - Allows the owner to get details about the orders placed for his products - year wise
    @ApiOperation(value = "Gives details about how many orders the owner has got")
    @GetMapping(value = "/analytics-yearly",produces = MediaType.APPLICATION_JSON_VALUE) //onClickDashboardYearlyWiseData
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<Year, Map<YearMonth, Map<String, Object>>>> onClickDashboardYearWiseData(HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Calling service layer to get order details - year wise
        Map<Year, Map<YearMonth, Map<String, Object>>> body = orderService.onClickDashboardYearWiseData(user);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API - Allows the owner to get details about the orders placed for him - month wise
    @GetMapping(value = "/monthly-order-items",produces = MediaType.APPLICATION_JSON_VALUE) //dashboardOrderItems
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> getOrderItemsDashboard(HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        // Calling service layer to get order details - month wise
        Map<YearMonth, List<OrderReceivedDto>> body = orderService.getOrderedItemsByMonth(user);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API - Allows the owner to get details about the orders placed for his products - subcategory wise
    @GetMapping(value = "/subcategories-analytics",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> getOrderItemsBySubCategories(HttpServletRequest request) {

        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Calling the service layer to get order details - Subcategory wise
        Map<YearMonth, Map<String, OrderItemsData>> body = orderService.getOrderItemsBySubCategories(user);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API - Allows an owner to get details of the order placed for his product - category wise
    @GetMapping(value = "/categories-analytics", produces = MediaType.APPLICATION_JSON_VALUE) //dashboardCategoriesAnalytics
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> getOrderItemsByCategories(HttpServletRequest request) {

        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Calling service layer to get order details - category wise
        Map<YearMonth, Map<String, OrderItemsData>> body = orderService.getOrderItemsByCategories(user);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API - Allows the owner to get details about the orders placed for his product - by selecting dates
    @GetMapping(value = "/date-selector",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> getOrderItemsDashboardBwDates(HttpServletRequest request, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);


        // Calling the service layer to get order details - date wise
        Map<YearMonth, List<OrderReceivedDto>> body = orderService.getOrderedItemsByMonthBwDates(user, startDate, endDate);

        // Status Code : 200-HttpStatus.OK
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
