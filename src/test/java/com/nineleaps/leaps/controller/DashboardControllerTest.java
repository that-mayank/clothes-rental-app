package com.nineleaps.leaps.controller;


import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import org.apache.http.annotation.Obsolete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@Tag("unit_tests")
@DisplayName("Test case file for dashboardController test")
class DashboardControllerTest {

    @Mock
    private DashboardServiceInterface dashboardService;

    @Mock
    private Helper helper;

    @Mock
    private OrderServiceInterface orderServiceInterface;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Get dashboard")
    void testDashboard() throws AuthenticationFailException {
        User user = new User();

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of dashboardService.dashboardOwnerView
        Map<String, Object> mockDashboardData = new HashMap<>();
        mockDashboardData.put("ordersCount", 10);
        mockDashboardData.put("totalEarnings", 2000);
        when(dashboardService.dashboardOwnerView(user)).thenReturn(mockDashboardData);

        // Call the API method
        ResponseEntity<Map<String, Object>> responseEntity = dashboardController.dashboard(mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(10, Objects.requireNonNull(responseEntity.getBody()).get("ordersCount"));
        assertEquals(2000, responseEntity.getBody().get("totalEarnings"));
    }

    @Test
    @DisplayName("On click dashboard get details")
    void testOnClickDashboard() {
        User user = new User();

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of dashboardService.analytics
        Map<YearMonth, Map<String, Object>> mockAnalyticsData = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(2023, 9);
        Map<String, Object> analyticsDetails = new HashMap<>();
        analyticsDetails.put("orderCount", 15);
        mockAnalyticsData.put(yearMonth, analyticsDetails);
        when(dashboardService.analytics(user)).thenReturn(mockAnalyticsData);

        // Call the API method
        ResponseEntity<Map<YearMonth, Map<String, Object>>> responseEntity = dashboardController.onClickDashboard(mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());

        Map<YearMonth, Map<String, Object>> responseAnalyticsData = responseEntity.getBody();
        assertEquals(15, responseAnalyticsData.get(yearMonth).get("orderCount"));
    }

    @Test
    @DisplayName("Get year wise data")
    void testOnClickDashboardYearWiseData() {
        User user = new User();

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of orderService.onClickDashboardYearWiseData
        Map<Year, Map<YearMonth, Map<String, Object>>> mockYearlyData = new HashMap<>();
        Year year = Year.of(2023);
        Map<YearMonth, Map<String, Object>> yearlyDetails = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(2023, 9);
        Map<String, Object> monthlyDetails = new HashMap<>();
        monthlyDetails.put("orderCount", 15);
        yearlyDetails.put(yearMonth, monthlyDetails);
        mockYearlyData.put(year, yearlyDetails);
        when(orderServiceInterface.onClickDashboardYearWiseData(user)).thenReturn(mockYearlyData);

        // Call the API method
        ResponseEntity<Map<Year, Map<YearMonth, Map<String, Object>>>> responseEntity = dashboardController.onClickDashboardYearWiseData(mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());

        Map<Year, Map<YearMonth, Map<String, Object>>> responseYearlyData = responseEntity.getBody();
        assertEquals(15, responseYearlyData.get(year).get(yearMonth).get("orderCount"));
    }

    @Test
    @DisplayName("get order items")
    void testGetOrderItemsDashboard() {
        User user = new User();

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of orderService.getOrderedItemsByMonth
        Map<YearMonth, List<OrderReceivedDto>> mockOrderItemsByMonth = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(2023, 9);
        List<OrderReceivedDto> orderReceivedDtos = new ArrayList<>();
        // Add some mock order items
        orderReceivedDtos.add(new OrderReceivedDto());
        mockOrderItemsByMonth.put(yearMonth, orderReceivedDtos);
        when(orderServiceInterface.getOrderedItemsByMonth(user)).thenReturn(mockOrderItemsByMonth);

        // Call the API method
        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = dashboardController.getOrderItemsDashboard(mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());

        Map<YearMonth, List<OrderReceivedDto>> responseOrderItemsByMonth = responseEntity.getBody();
        assertEquals(1, responseOrderItemsByMonth.get(yearMonth).size()); // Check the number of order items
    }

    @Test
    @DisplayName("get order items by sub categories")
    void testGetOrderItemsBySubCategories() {
        User user = new User();

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of orderService.getOrderItemsBySubCategories
        Map<YearMonth, Map<String, OrderItemsData>> mockOrderItemsBySubCategories = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(2023, 9);
        Map<String, OrderItemsData> orderItemsDataMap = new HashMap<>();
        // Add some mock order items data
        orderItemsDataMap.put("Subcategory1", new OrderItemsData());
        mockOrderItemsBySubCategories.put(yearMonth, orderItemsDataMap);
        when(orderServiceInterface.getOrderItemsBySubCategories(user)).thenReturn(mockOrderItemsBySubCategories);

        // Call the API method
        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = dashboardController.getOrderItemsBySubCategories(mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());

        Map<YearMonth, Map<String, OrderItemsData>> responseOrderItemsBySubCategories = responseEntity.getBody();
        assertEquals(1, responseOrderItemsBySubCategories.get(yearMonth).size()); // Check the number of order items data
    }

    @Test
    @DisplayName("get order items by categories")
    void testGetOrderItemsByCategories() {
        User user = new User();

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of orderService.getOrderItemsByCategories
        Map<YearMonth, Map<String, OrderItemsData>> mockOrderItemsByCategories = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(2023, 9);
        Map<String, OrderItemsData> orderItemsDataMap = new HashMap<>();
        // Add some mock order items data
        orderItemsDataMap.put("Category1", new OrderItemsData());
        mockOrderItemsByCategories.put(yearMonth, orderItemsDataMap);
        when(orderServiceInterface.getOrderItemsByCategories(user)).thenReturn(mockOrderItemsByCategories);

        // Call the API method
        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = dashboardController.getOrderItemsByCategories(mock(HttpServletRequest.class));

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());

        Map<YearMonth, Map<String, OrderItemsData>> responseOrderItemsByCategories = responseEntity.getBody();
        assertEquals(1, responseOrderItemsByCategories.get(yearMonth).size()); // Check the number of order items data
    }

    @Test
    @DisplayName("get order items between dates")
    void testGetOrderItemsDashboardBwDates() {
        User user = new User(); // Replace with a valid user

        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        // Mock the behavior of helper.getUserFromToken
        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);

        // Mock the behavior of orderService.getOrderedItemsByMonthBwDates
        Map<YearMonth, List<OrderReceivedDto>> mockOrderItemsByDates = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(2023, 9);
        List<OrderReceivedDto> orderItemsDataList = List.of(new OrderReceivedDto());
        mockOrderItemsByDates.put(yearMonth, orderItemsDataList);
        when(orderServiceInterface.getOrderedItemsByMonthBwDates(user, startDate, endDate)).thenReturn(mockOrderItemsByDates);

        // Call the API method
        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = dashboardController.getOrderItemsDashboardBwDates(mock(HttpServletRequest.class), startDate, endDate);

        // Check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());

        Map<YearMonth, List<OrderReceivedDto>> responseOrderItemsByDates = responseEntity.getBody();
        assertEquals(1, responseOrderItemsByDates.get(yearMonth).size()); // Check the number of order items data
    }

}