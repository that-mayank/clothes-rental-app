package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.dashboard.DashboardAnalyticsDto;
import com.nineleaps.leaps.dto.dashboard.DashboardDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Validated
@Api(tags = "Category Api")

public class DashboardController {

    //Linking layers using constructor injection

    private final DashboardServiceInterface dashboardService;
    private final OrderServiceInterface orderService;
    private final Helper helper;

    // API : Gives details about how many orders the owner has got and the total earnings

    @ApiOperation(value = "API : Gives details about how many orders the owner has got and the total earnings")
    @GetMapping(value = "/owner-view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    public ResponseEntity<DashboardDto> dashboard(HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        // Calling service layer to get details

        return new ResponseEntity<>(dashboardService.dashboardOwnerView(user), HttpStatus.OK);
    }

    // API : Gives details about how many orders the owner has got month wise

    @ApiOperation(value = "API : Gives details about how many orders the owner has got month wise")
    @GetMapping(value = "/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    public ResponseEntity<List<DashboardAnalyticsDto>> onClickDashboard(HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);

        // Calling service layer to get DashboardAnalyticsDto

       List<DashboardAnalyticsDto> body = dashboardService.analytics(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // API : Gives details about how many orders the owner has got month wise in provided year

    @ApiOperation(value = "API : Gives details about how many orders the owner has got month wise in provided year")
    @GetMapping("/analytics-yearly")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    public ResponseEntity<Map<Year, Map<YearMonth, Map<String, Object>>>> onClickDashboardYearWiseData(HttpServletRequest request) {

        // JWT : Extracting user info from token

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);


        Map<Year, Map<YearMonth, Map<String, Object>>> body = orderService.onClickDashboardYearWiseData(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/monthly-order-items") //dashboardOrderItems\
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    public ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> getOrderItemsDashboard(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, List<OrderReceivedDto>> body = orderService.getOrderedItemsByMonth(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/subcategories-analytics")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    public ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> getOrderItemsBySubCategories(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, Map<String, OrderItemsData>> body = orderService.getOrderItemsBySubCategories(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/categories-analytics") //dashboardCategoriesAnalytics
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    public ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> getOrderItemsByCategories(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, Map<String, OrderItemsData>> body = orderService.getOrderItemsByCategories(user);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/date-selector")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('OWNER')")

    public ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> getOrderItemsDashboardBwDates(HttpServletRequest request, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring(7);
        User user = helper.getUser(token);
        Map<YearMonth, List<OrderReceivedDto>> body = orderService.getOrderedItemsByMonthBwDates(user, startDate, endDate);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
