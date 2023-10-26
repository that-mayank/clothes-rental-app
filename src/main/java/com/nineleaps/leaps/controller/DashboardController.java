package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        User user = helper.getUserFromToken(request);
        try {
            log.info("Owner dashboard accessed by User={}", user.getEmail());
            Map<String, Object> result = dashboardService.dashboardOwnerView(user);
            log.debug("Owner dashboard result: {}", result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Owner dashboard for User={}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API - Allows the owner to get details about the total number of the order owner has got
    @ApiOperation(value = "Gives details about how many orders the owner has got")
    @GetMapping(value = "/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, Map<String, Object>>> onClickDashboard(HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);
        try {
            log.info("Analytics dashboard accessed by User={}", user.getEmail());
            Map<YearMonth, Map<String, Object>> body = dashboardService.analytics(user);
            log.debug("Analytics dashboard result: {}", body);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Analytics dashboard for User={}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API - Allows the owner to get details about the orders placed for his products - year wise
    @ApiOperation(value = "Gives details about how many orders the owner has got")
    @GetMapping(value = "/analytics-yearly",produces = MediaType.APPLICATION_JSON_VALUE) //onClickDashboardYearlyWiseData
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<Year, Map<YearMonth, Map<String, Object>>>> onClickDashboardYearWiseData(HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);
        try {
            log.info("Yearly Analytics dashboard accessed by User={}", user.getEmail());
            Map<Year, Map<YearMonth, Map<String, Object>>> body = orderService.onClickDashboardYearWiseData(user);
            log.debug("Yearly Analytics dashboard result: {}", body);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Yearly Analytics dashboard for User={}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Gives details about how many orders the owner has got monthly")
    // API - Allows the owner to get details about the orders placed for him - month wise
    @GetMapping(value = "/monthly-order-items",produces = MediaType.APPLICATION_JSON_VALUE) //dashboardOrderItems
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> getOrderItemsDashboard(HttpServletRequest request) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);

        try {
            log.info("Monthly Order Items dashboard accessed by User={}", user.getEmail());
            Map<YearMonth, List<OrderReceivedDto>> body = orderService.getOrderedItemsByMonth(user);
            log.debug("Monthly Order Items dashboard result: {}", body);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Monthly Order Items dashboard for User={}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API - Allows the owner to get details about the orders placed for his products - subcategory wise
    @GetMapping(value = "/subcategories-analytics",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> getOrderItemsBySubCategories(HttpServletRequest request) {

        // Extract User from the token
        User user = helper.getUserFromToken(request);
        try {
            log.info("Subcategories Analytics dashboard accessed by User={}", user.getEmail());
            Map<YearMonth, Map<String, OrderItemsData>> body = orderService.getOrderItemsBySubCategories(user);
            log.debug("Subcategories Analytics dashboard result: {}", body);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Subcategories Analytics dashboard for User={}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API - Allows an owner to get details of the order placed for his product - category wise
    @GetMapping(value = "/categories-analytics", produces = MediaType.APPLICATION_JSON_VALUE) //dashboardCategoriesAnalytics
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> getOrderItemsByCategories(HttpServletRequest request) {

        // Extract User from the token
        User user = helper.getUserFromToken(request);
        try {
            log.info("Categories Analytics dashboard accessed by User={}", user.getEmail());
            Map<YearMonth, Map<String, OrderItemsData>> body = orderService.getOrderItemsByCategories(user);
            log.debug("Categories Analytics dashboard result: {}", body);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Categories Analytics dashboard for User={}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API - Allows the owner to get details about the orders placed for his product - by selecting dates
    @GetMapping(value = "/date-selector",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')") // Adding Method Level Authorization Via RBAC - Role-Based Access Control
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> getOrderItemsDashboardBwDates(HttpServletRequest request, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        // Extract User from the token
        User user = helper.getUserFromToken(request);
        try {
            log.info("Date Selector dashboard accessed by User={}", user.getEmail());
            Map<YearMonth, List<OrderReceivedDto>> body = orderService.getOrderedItemsByMonthBwDates(user, startDate, endDate);
            log.debug("Date Selector dashboard result: {}", body);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Date Selector dashboard for User={}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
