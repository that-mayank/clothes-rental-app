package com.nineleaps.leaps.model.orders;

import com.nineleaps.leaps.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
    }

    @Test
    void getId() {
        order.setId(1L);
        assertEquals(1L, order.getId());
    }

    @Test
    void getCreateDate() {
        LocalDateTime createDate = LocalDateTime.now();
        order.setCreateDate(createDate);
        assertEquals(createDate, order.getCreateDate());
    }

    @Test
    void getTotalPrice() {
        order.setTotalPrice(99.99);
        assertEquals(99.99, order.getTotalPrice(), 0.001);
    }

    @Test
    void getSessionId() {
        order.setSessionId("session123");
        assertEquals("session123", order.getSessionId());
    }

    @Test
    void getOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        order.setOrderItems(orderItems);
        assertEquals(orderItems, order.getOrderItems());
    }

    @Test
    void getUser() {
        User user = new User();
        order.setUser(user);
        assertEquals(user, order.getUser());
    }

    @Test
    void setId() {
        order.setId(2L);
        assertEquals(2L, order.getId());
    }

    @Test
    void setCreateDate() {
        LocalDateTime createDate = LocalDateTime.now();
        order.setCreateDate(createDate);
        assertEquals(createDate, order.getCreateDate());
    }

    @Test
    void setTotalPrice() {
        order.setTotalPrice(49.99);
        assertEquals(49.99, order.getTotalPrice(), 0.001);
    }

    @Test
    void setSessionId() {
        order.setSessionId("session456");
        assertEquals("session456", order.getSessionId());
    }

    @Test
    void setOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        order.setOrderItems(orderItems);
        assertEquals(orderItems, order.getOrderItems());
    }

    @Test
    void setUser() {
        User user = new User();
        order.setUser(user);
        assertEquals(user, order.getUser());
    }
}
