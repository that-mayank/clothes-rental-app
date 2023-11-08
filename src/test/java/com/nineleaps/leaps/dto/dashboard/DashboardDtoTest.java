package com.nineleaps.leaps.dto.dashboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardDtoTest {

    private DashboardDto dashboardDto;

    @BeforeEach
    void setUp() {
        // Create an instance of DashboardDto for testing
        dashboardDto = new DashboardDto();
    }

    @Test
    void getTotalOrders() {
        // Test the getter method for totalOrders
        dashboardDto.setTotalOrders(10);
        assertEquals(10, dashboardDto.getTotalOrders());
    }

    @Test
    void getTotalEarnings() {
        // Test the getter method for totalEarnings
        dashboardDto.setTotalEarnings(500.0);
        assertEquals(500.0, dashboardDto.getTotalEarnings(), 0.001);
    }

    @Test
    void setTotalOrders() {
        // Test the setter method for totalOrders
        dashboardDto.setTotalOrders(15);
        assertEquals(15, dashboardDto.getTotalOrders());
    }

    @Test
    void setTotalEarnings() {
        // Test the setter method for totalEarnings
        dashboardDto.setTotalEarnings(750.0);
        assertEquals(750.0, dashboardDto.getTotalEarnings(), 0.001);
    }
}
