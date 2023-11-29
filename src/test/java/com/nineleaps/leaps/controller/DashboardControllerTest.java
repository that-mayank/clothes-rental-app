//package com.nineleaps.leaps.controller;
//
//
//import com.nineleaps.leaps.RuntimeBenchmarkExtension;
//import com.nineleaps.leaps.dto.orders.OrderItemsData;
//import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
//import com.nineleaps.leaps.model.User;
//import com.nineleaps.leaps.service.DashboardServiceInterface;
//import com.nineleaps.leaps.service.OrderServiceInterface;
//import com.nineleaps.leaps.utils.Helper;
//import com.nineleaps.leaps.exceptions.AuthenticationFailException;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import javax.servlet.http.HttpServletRequest;
//import java.time.LocalDateTime;
//import java.time.Year;
//import java.time.YearMonth;
//import java.util.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@Tag("unit_tests")
//@DisplayName("Test case file for dashboardController test")
//@ExtendWith(RuntimeBenchmarkExtension.class)
//class DashboardControllerTest {
//
//    @Mock
//    private DashboardServiceInterface dashboardService;
//
//    @Mock
//    private Helper helper;
//
//    @Mock
//    private OrderServiceInterface orderServiceInterface;
//
//    @InjectMocks
//    private DashboardController dashboardController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("Get dashboard")
//    void testDashboard() throws AuthenticationFailException {
//        User user = new User();
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of dashboardService.dashboardOwnerView
//        Map<String, Object> mockDashboardData = new HashMap<>();
//        mockDashboardData.put("ordersCount", 10);
//        mockDashboardData.put("totalEarnings", 2000);
//        when(dashboardService.dashboardOwnerView(user)).thenReturn(mockDashboardData);
//
//        // Call the API method
//        ResponseEntity<Map<String, Object>> responseEntity = dashboardController.dashboard(mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(10, Objects.requireNonNull(responseEntity.getBody()).get("ordersCount"));
//        assertEquals(2000, responseEntity.getBody().get("totalEarnings"));
//    }
//
//    @Test
//    @DisplayName("On click dashboard get details")
//    void testOnClickDashboard() {
//        User user = new User();
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of dashboardService.analytics
//        Map<YearMonth, Map<String, Object>> mockAnalyticsData = new HashMap<>();
//        YearMonth yearMonth = YearMonth.of(2023, 9);
//        Map<String, Object> analyticsDetails = new HashMap<>();
//        analyticsDetails.put("orderCount", 15);
//        mockAnalyticsData.put(yearMonth, analyticsDetails);
//        when(dashboardService.analytics(user)).thenReturn(mockAnalyticsData);
//
//        // Call the API method
//        ResponseEntity<Map<YearMonth, Map<String, Object>>> responseEntity = dashboardController.onClickDashboard(mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
//
//        Map<YearMonth, Map<String, Object>> responseAnalyticsData = responseEntity.getBody();
//        assertEquals(15, responseAnalyticsData.get(yearMonth).get("orderCount"));
//    }
//
//    @Test
//     void testDashboardCatchBlock() {
//        // Create a mock HttpServletRequest and User
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User(); // You may need to initialize this properly
//
//        // Simulate an exception when dashboardService.dashboardOwnerView is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        doAnswer(invocation -> {
//            throw new RuntimeException("Simulated Exception");
//        }).when(dashboardService).dashboardOwnerView(any(User.class));
//
//        // Call the dashboard method
//        ResponseEntity<Map<String, Object>> responseEntity = dashboardController.dashboard(request);
//
//        // Verify the response status code
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("Get year wise data")
//    void testOnClickDashboardYearWiseData() {
//        User user = new User();
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of orderService.onClickDashboardYearWiseData
//        Map<Year, Map<YearMonth, Map<String, Object>>> mockYearlyData = new HashMap<>();
//        Year year = Year.of(2023);
//        Map<YearMonth, Map<String, Object>> yearlyDetails = new HashMap<>();
//        YearMonth yearMonth = YearMonth.of(2023, 9);
//        Map<String, Object> monthlyDetails = new HashMap<>();
//        monthlyDetails.put("orderCount", 15);
//        yearlyDetails.put(yearMonth, monthlyDetails);
//        mockYearlyData.put(year, yearlyDetails);
//        when(orderServiceInterface.onClickDashboardYearWiseData(user)).thenReturn(mockYearlyData);
//
//        // Call the API method
//        ResponseEntity<Map<Year, Map<YearMonth, Map<String, Object>>>> responseEntity = dashboardController.onClickDashboardYearWiseData(mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
//
//        Map<Year, Map<YearMonth, Map<String, Object>>> responseYearlyData = responseEntity.getBody();
//        assertEquals(15, responseYearlyData.get(year).get(yearMonth).get("orderCount"));
//    }
//
//    @Test
//     void testOnClickDashboardCatchBlock() {
//        // Create a mock HttpServletRequest and User
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User(); // You may need to initialize this properly
//
//        // Simulate an exception when dashboardService.analytics is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        doAnswer(invocation -> {
//            throw new RuntimeException("Simulated Exception");
//        }).when(dashboardService).analytics(any(User.class));
//
//        // Call the onClickDashboard method
//        ResponseEntity<Map<YearMonth, Map<String, Object>>> responseEntity = dashboardController.onClickDashboard(request);
//
//        // Verify the response status code
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("get order items")
//    void testGetOrderItemsDashboard() {
//        User user = new User();
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of orderService.getOrderedItemsByMonth
//        Map<YearMonth, List<OrderReceivedDto>> mockOrderItemsByMonth = new HashMap<>();
//        YearMonth yearMonth = YearMonth.of(2023, 9);
//        List<OrderReceivedDto> orderReceivedDtos = new ArrayList<>();
//        // Add some mock order items
//        orderReceivedDtos.add(new OrderReceivedDto());
//        mockOrderItemsByMonth.put(yearMonth, orderReceivedDtos);
//        when(orderServiceInterface.getOrderedItemsByMonth(user)).thenReturn(mockOrderItemsByMonth);
//
//        // Call the API method
//        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = dashboardController.getOrderItemsDashboard(mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
//
//        Map<YearMonth, List<OrderReceivedDto>> responseOrderItemsByMonth = responseEntity.getBody();
//        assertEquals(1, responseOrderItemsByMonth.get(yearMonth).size()); // Check the number of order items
//    }
//
//    @Test
//     void testCatchBlockInOnClickDashboard() {
//        // Create a mock HttpServletRequest and User
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User(); // You may need to initialize this properly
//
//        // Simulate an exception when dashboardService.analytics is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        doAnswer(invocation -> {
//            throw new RuntimeException("Simulated Exception");
//        }).when(dashboardService).analytics(any(User.class));
//
//        // Call the onClickDashboard method
//        ResponseEntity<Map<YearMonth, Map<String, Object>>> responseEntity = dashboardController.onClickDashboard(request);
//
//        // Verify the response status code
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("get order items by sub categories")
//    void testGetOrderItemsBySubCategories() {
//        User user = new User();
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of orderService.getOrderItemsBySubCategories
//        Map<YearMonth, Map<String, OrderItemsData>> mockOrderItemsBySubCategories = new HashMap<>();
//        YearMonth yearMonth = YearMonth.of(2023, 9);
//        Map<String, OrderItemsData> orderItemsDataMap = new HashMap<>();
//        // Add some mock order items data
//        orderItemsDataMap.put("Subcategory1", new OrderItemsData());
//        mockOrderItemsBySubCategories.put(yearMonth, orderItemsDataMap);
//        when(orderServiceInterface.getOrderItemsBySubCategories(user)).thenReturn(mockOrderItemsBySubCategories);
//
//        // Call the API method
//        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = dashboardController.getOrderItemsBySubCategories(mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
//
//        Map<YearMonth, Map<String, OrderItemsData>> responseOrderItemsBySubCategories = responseEntity.getBody();
//        assertEquals(1, responseOrderItemsBySubCategories.get(yearMonth).size()); // Check the number of order items data
//    }
//
//    @Test
//    @DisplayName("get order items by categories")
//    void testGetOrderItemsByCategories() {
//        User user = new User();
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of orderService.getOrderItemsByCategories
//        Map<YearMonth, Map<String, OrderItemsData>> mockOrderItemsByCategories = new HashMap<>();
//        YearMonth yearMonth = YearMonth.of(2023, 9);
//        Map<String, OrderItemsData> orderItemsDataMap = new HashMap<>();
//        // Add some mock order items data
//        orderItemsDataMap.put("Category1", new OrderItemsData());
//        mockOrderItemsByCategories.put(yearMonth, orderItemsDataMap);
//        when(orderServiceInterface.getOrderItemsByCategories(user)).thenReturn(mockOrderItemsByCategories);
//
//        // Call the API method
//        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = dashboardController.getOrderItemsByCategories(mock(HttpServletRequest.class));
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
//
//        Map<YearMonth, Map<String, OrderItemsData>> responseOrderItemsByCategories = responseEntity.getBody();
//        assertEquals(1, responseOrderItemsByCategories.get(yearMonth).size()); // Check the number of order items data
//    }
//
//
//    @Test
//     void testCatchBlockInOnClickDashboardYearWiseData() {
//        // Create a mock HttpServletRequest and User
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User(); // You may need to initialize this properly
//
//        // Simulate an exception when orderService.onClickDashboardYearWiseData is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        doAnswer(invocation -> {
//            throw new RuntimeException("Simulated Exception");
//        }).when(orderServiceInterface).onClickDashboardYearWiseData(any(User.class));
//
//        // Call the onClickDashboardYearWiseData method
//        ResponseEntity<Map<Year, Map<YearMonth, Map<String, Object>>>> responseEntity = dashboardController.onClickDashboardYearWiseData(request);
//
//
//        // Verify the response status code
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//    @Test
//    @DisplayName("get order items between dates")
//    void testGetOrderItemsDashboardBwDates() {
//        User user = new User(); // Replace with a valid user
//
//        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
//        LocalDateTime endDate = LocalDateTime.now();
//
//        // Mock the behavior of helper.getUserFromToken
//        when(helper.getUserFromToken(any(HttpServletRequest.class))).thenReturn(user);
//
//        // Mock the behavior of orderService.getOrderedItemsByMonthBwDates
//        Map<YearMonth, List<OrderReceivedDto>> mockOrderItemsByDates = new HashMap<>();
//        YearMonth yearMonth = YearMonth.of(2023, 9);
//        List<OrderReceivedDto> orderItemsDataList = List.of(new OrderReceivedDto());
//        mockOrderItemsByDates.put(yearMonth, orderItemsDataList);
//        when(orderServiceInterface.getOrderedItemsByMonthBwDates(user, startDate, endDate)).thenReturn(mockOrderItemsByDates);
//
//        // Call the API method
//        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = dashboardController.getOrderItemsDashboardBwDates(mock(HttpServletRequest.class), startDate, endDate);
//
//        // Check the response
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
//
//        Map<YearMonth, List<OrderReceivedDto>> responseOrderItemsByDates = responseEntity.getBody();
//        assertEquals(1, responseOrderItemsByDates.get(yearMonth).size()); // Check the number of order items data
//    }
//
//    @Test
//     void testCatchBlockInGetOrderItemsDashboard() {
//        // Create a mock HttpServletRequest and User
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User(); // You may need to initialize this properly
//
//        // Simulate an exception when orderService.getOrderedItemsByMonth is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        doAnswer(invocation -> {
//            throw new RuntimeException("Simulated Exception");
//        }).when(orderServiceInterface).getOrderedItemsByMonth(any(User.class));
//
//        // Call the getOrderItemsDashboard method
//        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = dashboardController.getOrderItemsDashboard(request);
//
//        // Verify the response status code
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//
//
//    @Test
//     void testCatchBlockInGetOrderItemsBySubCategories() {
//        // Create a mock HttpServletRequest and User
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User(); // You may need to initialize this properly
//
//        // Simulate an exception when orderService.getOrderItemsBySubCategories is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        doAnswer(invocation -> {
//            throw new RuntimeException("Simulated Exception");
//        }).when(orderServiceInterface).getOrderItemsBySubCategories(any(User.class));
//
//        // Call the getOrderItemsBySubCategories method
//        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = dashboardController.getOrderItemsBySubCategories(request);
//
//
//        // Verify the response status code
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//
//    @Test
//     void testCatchBlockInGetOrderItemsByCategories() {
//        // Create a mock HttpServletRequest and User
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User(); // You may need to initialize this properly
//
//        // Simulate an exception when orderService.getOrderItemsByCategories is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        doAnswer(invocation -> {
//            throw new RuntimeException("Simulated Exception");
//        }).when(orderServiceInterface).getOrderItemsByCategories(any(User.class));
//
//        // Call the getOrderItemsByCategories method
//        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = dashboardController.getOrderItemsByCategories(request);
//
//        // Verify the response status code
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//
//    @Test
//     void testCatchBlockInGetOrderItemsDashboardBwDates() {
//        // Create a mock HttpServletRequest and User
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        User user = new User(); // You may need to initialize this properly
//        LocalDateTime startDate = LocalDateTime.now();
//        LocalDateTime endDate = LocalDateTime.now();
//
//        // Simulate an exception when orderService.getOrderedItemsByMonthBwDates is called
//        when(helper.getUserFromToken(request)).thenReturn(user);
//        doAnswer(invocation -> {
//            throw new RuntimeException("Simulated Exception");
//        }).when(orderServiceInterface).getOrderedItemsByMonthBwDates(any(User.class), eq(startDate), eq(endDate));
//
//        // Call the getOrderItemsDashboardBwDates method
//        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = dashboardController.getOrderItemsDashboardBwDates(request, startDate, endDate);
//
//
//        // Verify the response status code
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//
//}


package com.nineleaps.leaps.controller;

import com.nineleaps.leaps.dto.dashboard.DashboardAnalyticsDto;
import com.nineleaps.leaps.dto.dashboard.DashboardDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.nineleaps.leaps.utils.Helper;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("unit")
class DashboardControllerTest {

    @Mock
    private DashboardServiceInterface dashboardService;
    @Mock
    private OrderServiceInterface orderService;
    @Mock
    private Helper helper;
    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Dashboard - Success")
    void dashboard_ReturnsDashboardDto() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        DashboardDto dashboardDto = new DashboardDto();

        when(helper.getUserFromToken((request))).thenReturn(user);
        when(dashboardService.dashboardOwnerView(request)).thenReturn(dashboardDto);

        // Act
        ResponseEntity<DashboardDto> responseEntity = dashboardController.dashboard(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        DashboardDto resultDashboardDto = responseEntity.getBody();
        assertNotNull(resultDashboardDto);
        assertEquals(dashboardDto, resultDashboardDto);
    }

    @Test
    @DisplayName("On Click Dashboard - Success")
    void onClickDashboard_ReturnsDashboardAnalyticsDtoList() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        List<DashboardAnalyticsDto> dashboardAnalyticsDtoList = Collections.singletonList(new DashboardAnalyticsDto());

        when(helper.getUserFromToken((request))).thenReturn(user);
        when(dashboardService.analytics(request)).thenReturn(dashboardAnalyticsDtoList);

        // Act
        ResponseEntity<List<DashboardAnalyticsDto>> responseEntity = dashboardController.onClickDashboard(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<DashboardAnalyticsDto> resultDashboardAnalyticsDtoList = responseEntity.getBody();
        assertNotNull(resultDashboardAnalyticsDtoList);
        assertEquals(dashboardAnalyticsDtoList, resultDashboardAnalyticsDtoList);
    }

    @Test
    @DisplayName("On Click Dashboard Yearly- Success")
    void onClickDashboardYearWiseData_ReturnsYearlyDataMap() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Map<Year, Map<YearMonth, Map<String, Object>>> yearlyDataMap = Collections.singletonMap(Year.of(2023), Collections.emptyMap());

        when(helper.getUserFromToken((request))).thenReturn(user);
        when(orderService.onClickDashboardYearWiseData(request)).thenReturn(yearlyDataMap);

        // Act
        ResponseEntity<Map<Year, Map<YearMonth, Map<String, Object>>>> responseEntity = dashboardController.onClickDashboardYearWiseData(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<Year, Map<YearMonth, Map<String, Object>>> resultYearlyDataMap = responseEntity.getBody();
        assertNotNull(resultYearlyDataMap);
        assertEquals(yearlyDataMap, resultYearlyDataMap);
    }

    @Test
    @DisplayName("Get Order Items Dashboard - Success")
    void getOrderItemsDashboard_ReturnsMonthlyOrderItemsMap() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Map<YearMonth, List<OrderReceivedDto>> monthlyOrderItemsMap = Collections.singletonMap(YearMonth.of(2023, 9), Collections.emptyList());

        when(helper.getUserFromToken((request))).thenReturn(user);
        when(orderService.getOrderedItemsByMonth(request)).thenReturn(monthlyOrderItemsMap);

        // Act
        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = dashboardController.getOrderItemsDashboard(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<YearMonth, List<OrderReceivedDto>> resultMonthlyOrderItemsMap = responseEntity.getBody();
        assertNotNull(resultMonthlyOrderItemsMap);
        assertEquals(monthlyOrderItemsMap, resultMonthlyOrderItemsMap);
    }

    @Test
    @DisplayName("Get Order Items By SubCategories - Success")
    void getOrderItemsBySubCategories_ReturnsOrderItemsBySubCategoriesMap() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Map<YearMonth, Map<String, OrderItemsData>> orderItemsBySubCategoriesMap = Collections.singletonMap(
                YearMonth.of(2023, 9),
                Collections.singletonMap("CategoryName", new OrderItemsData())
        );

        when(helper.getUserFromToken((request))).thenReturn(user);
        when(orderService.getOrderItemsBySubCategories(request)).thenReturn(orderItemsBySubCategoriesMap);

        // Act
        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = dashboardController.getOrderItemsBySubCategories(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<YearMonth, Map<String, OrderItemsData>> resultOrderItemsBySubCategoriesMap = responseEntity.getBody();
        assertNotNull(resultOrderItemsBySubCategoriesMap);
        assertEquals(orderItemsBySubCategoriesMap, resultOrderItemsBySubCategoriesMap);
    }

    @Test
    @DisplayName("Get Order Items By Categories")
    void getOrderItemsByCategories_ReturnsOrderItemsByCategoriesMap() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        Map<YearMonth, Map<String, OrderItemsData>> orderItemsByCategoriesMap = Collections.singletonMap(
                YearMonth.of(2023, 9),
                Collections.singletonMap("CategoryName", new OrderItemsData())
        );

        when(helper.getUserFromToken((request))).thenReturn(user);
        when(orderService.getOrderItemsByCategories(request)).thenReturn(orderItemsByCategoriesMap);

        // Act
        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = dashboardController.getOrderItemsByCategories(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<YearMonth, Map<String, OrderItemsData>> resultOrderItemsByCategoriesMap = responseEntity.getBody();
        assertNotNull(resultOrderItemsByCategoriesMap);
        assertEquals(orderItemsByCategoriesMap, resultOrderItemsByCategoriesMap);
    }

    @Test
    @DisplayName("Get Order Items Dashboard Bw Dates")
    void getOrderItemsDashboardBwDates_ReturnsOrderItemsByDateRangeMap() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        LocalDateTime startDate = LocalDateTime.of(2023, 9, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 9, 30, 23, 59);
        Map<YearMonth, List<OrderReceivedDto>> orderItemsByDateRangeMap = Collections.singletonMap(
                YearMonth.of(2023, 9),
                Collections.emptyList()
        );

        when(helper.getUserFromToken((request))).thenReturn(user);
        when(orderService.getOrderedItemsByMonthBwDates(request, startDate, endDate)).thenReturn(orderItemsByDateRangeMap);

        // Act
        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = dashboardController.getOrderItemsDashboardBwDates(request, startDate, endDate);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<YearMonth, List<OrderReceivedDto>> resultOrderItemsByDateRangeMap = responseEntity.getBody();
        assertNotNull(resultOrderItemsByDateRangeMap);
        assertEquals(orderItemsByDateRangeMap, resultOrderItemsByDateRangeMap);
    }
}