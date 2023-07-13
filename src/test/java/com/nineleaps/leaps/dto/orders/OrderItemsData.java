package com.nineleaps.leaps.dto.orders;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderItemsDataTest {
    private OrderItemsData orderItemsData;

    @BeforeEach
    void setUp() {
        orderItemsData = new OrderItemsData();
    }

    @Test
    void testIncrementTotalOrders() {
        int initialTotalOrders = orderItemsData.getTotalOrders();
        int quantity = 5;

        orderItemsData.incrementTotalOrders(quantity);

        assertEquals(initialTotalOrders + quantity, orderItemsData.getTotalOrders());
    }

    @Test
    void testGetOrderItems() {
        List<OrderReceivedDto> orderItems = new ArrayList<>();
        orderItems.add(new OrderReceivedDto(createOrderItem()));
        orderItems.add(new OrderReceivedDto(createOrderItem()));

        orderItemsData.setOrderItems(orderItems);

        assertEquals(orderItems, orderItemsData.getOrderItems());
    }

    @Test
    void testGetTotalOrders() {
        int totalOrders = 10;
        orderItemsData.setTotalOrders(totalOrders);

        assertEquals(totalOrders, orderItemsData.getTotalOrders());
    }

    @Test
    void testSetOrderItems() {
        List<OrderReceivedDto> orderItems = new ArrayList<>();
        orderItems.add(new OrderReceivedDto(createOrderItem()));
        orderItems.add(new OrderReceivedDto(createOrderItem()));

        orderItemsData.setOrderItems(orderItems);

        assertEquals(orderItems, orderItemsData.getOrderItems());
    }

    @Test
    void testSetTotalOrders() {
        int totalOrders = 10;
        orderItemsData.setTotalOrders(totalOrders);

        assertEquals(totalOrders, orderItemsData.getTotalOrders());
    }

    private OrderItem createOrderItem() {
        OrderItem orderItem = new OrderItem();

        // Set properties of the OrderItem as per your test case
        orderItem.setId(1L);
        orderItem.setQuantity(1);
        orderItem.setPrice(9.99);
        orderItem.setCreatedDate(LocalDateTime.now());

        // Set the associated Product object for the OrderItem
        Product product = new Product();
        product.setId(1L);
        product.setName("Sample Product");
        // Set other properties of the Product object as needed
        orderItem.setProduct(product);

        // Set the associated Order object for the OrderItem
        Order order = new Order();
        order.setId(1L);

        // Set the associated User object for the Order
        User user = new User();
        user.setId(1L);
        // Set other properties of the User object as needed

        order.setUser(user);
        // Set other properties of the Order object as needed

        orderItem.setOrder(order);

        // Set the image URL
        orderItem.setImageUrl("/api/v1/file/view/test_image.png");

        // Set rental start date and end date
        LocalDateTime rentalStartDate = LocalDateTime.of(2023, 7, 1, 0, 0);
        LocalDateTime rentalEndDate = LocalDateTime.of(2023, 7, 5, 0, 0);
        orderItem.setRentalStartDate(rentalStartDate);
        orderItem.setRentalEndDate(rentalEndDate);

        return orderItem;
    }

}
