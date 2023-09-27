package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;

import com.nineleaps.leaps.model.product.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderDtoTest {

    @Test
    void orderDtoCreation() {
        // Create a sample order
        Order order = new Order();
        order.setId(1L);
        order.setCreateDate(LocalDateTime.now());
        order.setTotalPrice(100.0);

        // Create sample order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Product product = new Product();
            OrderItem orderItem = new OrderItem();
            orderItem.setId((long) i);
            orderItem.setQuantity(i * 2);
            orderItem.setPrice(i * 10.0);
            orderItem.setProduct(product);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        // Mock OrderItemDto creation
        List<OrderItemDto> expectedOrderItemDtos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemDto orderItemDto = mock(OrderItemDto.class);
            when(orderItemDto.getId()).thenReturn(orderItem.getId());
            when(orderItemDto.getQuantity()).thenReturn(orderItem.getQuantity());
            when(orderItemDto.getPricePerDay()).thenReturn(orderItem.getPrice());
            // Add more mocked behaviors as needed for OrderItemDto

            expectedOrderItemDtos.add(orderItemDto);
        }

        // Create the OrderDto
        OrderDto orderDto = new OrderDto(order);

        // Verify the properties of the OrderDto
        assertEquals(order.getId(), orderDto.getId());
        assertEquals(order.getCreateDate(), orderDto.getCreatedDate());
        assertEquals(order.getTotalPrice(), orderDto.getTotalPrice());

        // Verify the properties of each OrderItemDto
        List<OrderItemDto> actualOrderItemDtos = orderDto.getOrderItems();
        for (int i = 0; i < expectedOrderItemDtos.size(); i++) {
            OrderItemDto expectedOrderItemDto = expectedOrderItemDtos.get(i);
            OrderItemDto actualOrderItemDto = actualOrderItemDtos.get(i);

            assertEquals(expectedOrderItemDto.getId(), actualOrderItemDto.getId());
            assertEquals(expectedOrderItemDto.getQuantity(), actualOrderItemDto.getQuantity());
            assertEquals(expectedOrderItemDto.getPricePerDay(), actualOrderItemDto.getPricePerDay());
            // Add more assertions as needed for OrderItemDto
        }
    }
}
