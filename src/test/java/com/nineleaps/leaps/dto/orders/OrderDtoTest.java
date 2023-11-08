package com.nineleaps.leaps.dto.orders;


import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class OrderDtoTest {

    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        // Create a mock Order object for testing
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        Order order = new Order();
        order.setId(1L);
        order.setCreateDate(LocalDateTime.now());
        order.setTotalPrice(100.0);


        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(101L);
        orderItem1.setQuantity(1);
        orderItem1.setPrice(10.0);
        orderItem1.setProduct(product1);


        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(102L);
        orderItem2.setQuantity(1);
        orderItem2.setPrice(10.0);
        orderItem2.setProduct(product1);


        orderItems.add(orderItem1);
        orderItems.add(orderItem2);

        order.setOrderItems(orderItems);

        orderDto = new OrderDto(order);
    }

    @Test
    void getId() {
        assertEquals(1L, orderDto.getId());
    }

    @Test
    void getCreatedDate() {
        assertEquals(LocalDateTime.now().toLocalDate(), orderDto.getCreatedDate().toLocalDate());
    }

    @Test
    void getTotalPrice() {
        assertEquals(100.0, orderDto.getTotalPrice());
    }

    @Test
    void getOrderItems() {
        List<OrderItemDto> orderItems = orderDto.getOrderItems();
        assertEquals(2, orderItems.size());
        assertEquals(101L, orderItems.get(0).getId());
        assertEquals(102L, orderItems.get(1).getId());
    }

    @Test
    void setId() {
        orderDto.setId(2L);
        assertEquals(2L, orderDto.getId());
    }

    @Test
    void setCreatedDate() {
        orderDto.setCreatedDate(LocalDateTime.now());
        assertEquals(LocalDateTime.now().toLocalDate(), orderDto.getCreatedDate().toLocalDate());
    }

    @Test
    void setTotalPrice() {
        orderDto.setTotalPrice(200.0);
        assertEquals(200.0, orderDto.getTotalPrice());
    }

    @Test
    void setOrderItems() {
        List<OrderItemDto> newOrderItems = new ArrayList<>();
        newOrderItems.add(new OrderItemDto());
        orderDto.setOrderItems(newOrderItems);
        assertEquals(newOrderItems, orderDto.getOrderItems());
    }
}
