package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.dashboard.DashboardAnalyticsDto;
import com.nineleaps.leaps.dto.dashboard.DashboardDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DashboardServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void dashboardOwnerView() {

        // Create a user

        User user = new User();

        user.setId(1L);

        // Create a list of order items

        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem orderItem1 = new OrderItem();

        orderItem1.setOwnerId(1L);

        orderItem1.setPrice(10.0);

        orderItem1.setQuantity(2);

        orderItem1.setRentalStartDate(LocalDate.now().atStartOfDay());

        orderItem1.setRentalEndDate(LocalDate.now().plusDays(3).atStartOfDay());

        orderItems.add(orderItem1);

        when(orderItemRepository.findByOwnerId(1L)).thenReturn(orderItems);

        // Call the dashboardOwnerView method

        DashboardDto dashboardDto = dashboardService.dashboardOwnerView(request);

        // Verify that the method returns the expected result

        assertEquals(1, dashboardDto.getTotalOrders());

        assertEquals(60.0, dashboardDto.getTotalEarnings(), 0.01);

    }

    @Test
    void analytics() {

        // Create a user

        User user = new User();

        user.setId(1L);

        // Create a list of order items

        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem orderItem1 = new OrderItem();

        orderItem1.setOwnerId(1L);

        orderItem1.setPrice(10.0);

        orderItem1.setQuantity(2);

        orderItem1.setRentalStartDate(LocalDate.now().atStartOfDay());

        orderItem1.setRentalEndDate(LocalDate.now().plusDays(3).atStartOfDay());

        orderItem1.setCreatedDate(LocalDate.of(2023, 1, 15).atStartOfDay());

        orderItems.add(orderItem1);

        when(orderItemRepository.findByOwnerId(1L)).thenReturn(orderItems);

        // Call the analytics method

        List<DashboardAnalyticsDto> analyticsDtoList = dashboardService.analytics(request);

        // Verify that the method returns the expected result

        assertEquals(1, analyticsDtoList.size());

        DashboardAnalyticsDto analyticsDto = analyticsDtoList.get(0);

        assertEquals(LocalDate.of(2023, 1, 1).getMonth(), analyticsDto.getMonth().getMonth());

        assertEquals(2, analyticsDto.getTotalOrders());

        assertEquals(60.0, analyticsDto.getTotalEarnings(), 0.01);

    }

}

