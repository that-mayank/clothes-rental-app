package com.nineleaps.leaps.model.orders;

import com.nineleaps.leaps.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class OrderTest {

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        order = new Order();
        order.setId(1L);
        order.setCreateDate(LocalDateTime.now());
        order.setTotalPrice(99.99);
        order.setSessionId("abc123");
        order.setOrderItems(new ArrayList<>());
        order.setUser(user);
    }

    @Test
    void testConstructor() {
        assertNotNull(order.getId());
        assertNotNull(order.getCreateDate());
        assertEquals(99.99, order.getTotalPrice());
        assertEquals("abc123", order.getSessionId());
        assertNotNull(order.getOrderItems());
        assertEquals(user, order.getUser());
    }

    @Test
    void testGettersAndSetters() {
        assertNotNull(order.getId());
        assertNotNull(order.getCreateDate());
        assertEquals(99.99, order.getTotalPrice());
        assertEquals("abc123", order.getSessionId());
        assertNotNull(order.getOrderItems());
        assertEquals(user, order.getUser());
    }
}
