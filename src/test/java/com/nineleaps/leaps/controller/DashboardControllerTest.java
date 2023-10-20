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

        when(helper.getUser((request))).thenReturn(user);
        when(dashboardService.dashboardOwnerView(user)).thenReturn(dashboardDto);

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

        when(helper.getUser((request))).thenReturn(user);
        when(dashboardService.analytics(user)).thenReturn(dashboardAnalyticsDtoList);

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

        when(helper.getUser((request))).thenReturn(user);
        when(orderService.onClickDashboardYearWiseData(user)).thenReturn(yearlyDataMap);

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

        when(helper.getUser((request))).thenReturn(user);
        when(orderService.getOrderedItemsByMonth(user)).thenReturn(monthlyOrderItemsMap);

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

        when(helper.getUser((request))).thenReturn(user);
        when(orderService.getOrderItemsBySubCategories(user)).thenReturn(orderItemsBySubCategoriesMap);

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

        when(helper.getUser((request))).thenReturn(user);
        when(orderService.getOrderItemsByCategories(user)).thenReturn(orderItemsByCategoriesMap);

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

        when(helper.getUser((request))).thenReturn(user);
        when(orderService.getOrderedItemsByMonthBwDates(user, startDate, endDate)).thenReturn(orderItemsByDateRangeMap);

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