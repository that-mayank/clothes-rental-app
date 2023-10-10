package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import static com.nineleaps.leaps.config.MessageStrings.TOTAL_INCOME;
import static com.nineleaps.leaps.config.MessageStrings.TOTAL_NUMBER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@Tag("unit_tests")
@DisplayName("Dashboard Service Tests")
@ExtendWith(RuntimeBenchmarkExtension.class)
class DashboardServiceImplTest {


    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Dashboard Owner View: Empty Order List - Returns Zero Total Earnings and Items")
    void dashboardOwnerView_EmptyOrderList_ReturnsZeroTotalEarningsAndItems() {
        // Arrange
        User user = new User();
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = dashboardService.dashboardOwnerView(user);

        // Assert
        assertEquals(0.0, result.get(TOTAL_INCOME));
        assertEquals(0, result.get(TOTAL_NUMBER));
    }

    @Test
    @DisplayName("Analytics: Empty Order List - Returns Empty Result")
    void analytics_EmptyOrderList_ReturnsEmptyResult() {
        // Arrange
        User user = new User();
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        Map<YearMonth, Map<String, Object>> result = dashboardService.analytics(user);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Analytics: Processes Orders - Returns Correct Result")
    void analytics_ProcessesOrders_ReturnsCorrectResult() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        User user = new User();
        Product product = new Product();
        product.setUser(user);
        Order order = new Order();  // Create a valid Order instance
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setPrice(10.0);
        orderItem.setQuantity(2);
        orderItem.setRentalStartDate(LocalDateTime.now().minusDays(3));
        orderItem.setRentalEndDate(LocalDateTime.now());
        order.setOrderItems(Collections.singletonList(orderItem));
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));

        DashboardServiceImpl dashboardService = new DashboardServiceImpl(orderRepository);

        Method privateMethod = DashboardServiceImpl.class.getDeclaredMethod("analytics", User.class);
        privateMethod.setAccessible(true);

        // Act
        Map<YearMonth, Map<String, Object>> result = (Map<YearMonth, Map<String, Object>>) privateMethod.invoke(dashboardService, user);

        // Assert
        // Ensure the result map is generated correctly based on the order item
        assertEquals(1, result.size());

        // Ensure the month entry is correct
        YearMonth month = YearMonth.from(orderItem.getRentalStartDate());  // Use orderItem's rental start date
        assertTrue(result.containsKey(month));
        Map<String, Object> monthData = result.get(month);
        assertEquals(2, monthData.get(TOTAL_NUMBER));
        assertEquals(2 * 10.0 * 3, monthData.get(TOTAL_INCOME));  // Expected earnings = quantity * price * days
    }


    @Test
    @DisplayName("Calculate Total Earnings: Single Order With Single Item - Returns Correct Earnings")
    void calculateTotalEarnings_SingleOrderWithSingleItem_ReturnsCorrectEarnings() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        User user = new User();
        Product product = new Product();
        product.setUser(user);  // Set a valid User for the product
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setPrice(10.0);
        orderItem.setQuantity(2);
        orderItem.setRentalStartDate(LocalDateTime.now().minusDays(3));
        orderItem.setRentalEndDate(LocalDateTime.now());
        Order order = new Order();
        order.setOrderItems(Collections.singletonList(orderItem));
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));

        Method privateMethod = DashboardServiceImpl.class.getDeclaredMethod("calculateTotalEarnings", User.class);
        privateMethod.setAccessible(true);

        // Act
        double totalEarnings = (double) privateMethod.invoke(dashboardService, user);

        // Assert
        assertEquals(2 * 10.0 * 3, totalEarnings);

        // Additional assertion to cover if statement
        assertTrue(totalEarnings > 0);  // The if statement implies earnings are greater than 0
    }



    @Test
    @DisplayName("Calculate Total Number of Items: Single Order With Single Item - Returns Correct Number of Items")
    void calculateTotalNumberOfItems_SingleOrderWithSingleItem_ReturnsCorrectNumberOfItems()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        User user = new User();
        Product product = new Product();
        product.setUser(user); // Set a valid User for the product
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(5);
        Order order = new Order();
        order.setOrderItems(Collections.singletonList(orderItem));
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));

        DashboardServiceImpl dashboardService = new DashboardServiceImpl(orderRepository);

        Method privateMethod = DashboardServiceImpl.class.getDeclaredMethod("calculateTotalNumberOfItems", User.class);
        privateMethod.setAccessible(true);

        // Act
        int totalNumberOfItems = (int) privateMethod.invoke(dashboardService, user);

        // Assert
        assertEquals(5, totalNumberOfItems);
    }

    @Test
    @DisplayName("Process Order Items: Single Order With Single Item - Correctly Processes Items")
    void processOrderItems_SingleOrderWithSingleItem_CorrectlyProcessesItems()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        User user = new User();
        Product product = new Product();
        product.setUser(user); // Set a valid User for the product
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(5);
        orderItem.setPrice(10.0);
        orderItem.setRentalStartDate(LocalDateTime.now().minusDays(3));
        orderItem.setRentalEndDate(LocalDateTime.now());
        Order order = new Order();
        order.setOrderItems(Collections.singletonList(orderItem));

        DashboardServiceImpl dashboardService = new DashboardServiceImpl(orderRepository);

        Method privateMethod = DashboardServiceImpl.class.getDeclaredMethod(
                "processOrderItems", Order.class, User.class, Map.class, Map.class);
        privateMethod.setAccessible(true);

        Map<YearMonth, Double> totalEarningsByMonth = new HashMap<>();
        Map<YearMonth, Integer> totalItemsByMonth = new HashMap<>();

        // Act
        privateMethod.invoke(dashboardService, order, user, totalEarningsByMonth, totalItemsByMonth);

        // Assert
        // Ensure the maps are correctly updated based on the order item
        assertEquals(1, totalEarningsByMonth.size());
        assertEquals(1, totalItemsByMonth.size());

        // Ensure total earnings and total items are correct
        double expectedEarnings = orderItem.getPrice() * orderItem.getQuantity() * 3; // 3 days rental
        assertEquals(expectedEarnings, totalEarningsByMonth.values().iterator().next());

        int expectedTotalItems = orderItem.getQuantity();
        assertEquals(expectedTotalItems, totalItemsByMonth.values().iterator().next());
    }

}