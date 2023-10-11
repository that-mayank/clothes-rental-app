package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.notifications.PushNotificationRequest;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.service.CartServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks

    private OrderServiceImpl orderService;

    @Mock

    private OrderRepository orderRepository;

    @Mock

    private CartServiceInterface cartService;

    @Mock

    private OrderItemRepository orderItemRepository;

    @Mock

    private EmailServiceImpl emailService;

    @Mock

    private ProductRepository productRepository;

    @Mock

    private PushNotificationServiceImpl pushNotificationService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

    }

    @Test
    void placeOrder_Success() {

        // Arrange

        User user = new User();

        String razorpayId = "razorpay123";

        CartDto cartDto = new CartDto();

        List<CartItemDto> cartItemDtos = new ArrayList<>();

        cartDto.setCartItems(cartItemDtos);

        when(cartService.listCartItems(user)).thenReturn(cartDto);

        // Act

        assertDoesNotThrow(() -> orderService.placeOrder(user, razorpayId));

        // Assert

        verify(orderRepository, times(1)).save(any(Order.class));

        verify(orderItemRepository, times(0)).save(any(OrderItem.class));

        verify(productRepository, times(0)).save(any());

        verify(cartService, times(1)).deleteUserCartItems(user);

//        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());

        verify(pushNotificationService, times(0)).sendPushNotificationToToken(any(PushNotificationRequest.class));

    }

    @Test
    void placeOrder_WithCartItem() {

        // Arrange

        User user = new User();

        user.setId(3L);

        user.setDeviceToken("test");

        Product product = new Product();

        product.setId(1L);

        product.setUser(user);

        ProductUrl productUrl = new ProductUrl();

        productUrl.setId(1L);

        productUrl.setUrl("testUrl");

        product.setImageURL(List.of(productUrl, productUrl));

        String razorpayId = "razorpay123";

        CartDto cartDto = new CartDto();

        List<CartItemDto> cartItemDtos = new ArrayList<>();

        CartItemDto cartItemDto = new CartItemDto();

        cartItemDto.setProduct(product);

        cartItemDto.setRentalStartDate(LocalDateTime.now());

        cartItemDto.setRentalEndDate(LocalDateTime.now().plusDays(10));

        cartItemDtos.add(cartItemDto);

        cartDto.setCartItems(cartItemDtos);

        when(cartService.listCartItems(user)).thenReturn(cartDto);

        // Act

        assertDoesNotThrow(() -> orderService.placeOrder(user, razorpayId));

        // Assert

        verify(orderRepository, times(1)).save(any(Order.class));

        verify(orderItemRepository, times(1)).save(any(OrderItem.class));

        verify(productRepository, times(1)).save(any());

        verify(cartService, times(1)).deleteUserCartItems(user);

//        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());

        verify(pushNotificationService, times(1)).sendPushNotificationToToken(any(PushNotificationRequest.class));

    }

    @Test
    void placeOrder_NullCartItem() {

        // Arrange

        User user = new User();

        String razorpayId = "razorpay123";

        CartDto cartDto = new CartDto();

        List<CartItemDto> cartItemDtos = null;

        cartDto.setCartItems(cartItemDtos);

        when(cartService.listCartItems(user)).thenReturn(cartDto);

        // Act and Assert

        assertThrows(RuntimeException.class, () -> {

            // Your code that is expected to throw an exception

            orderService.placeOrder(user, razorpayId);

        });

        verify(orderRepository, times(0)).save(any(Order.class));

        verify(orderItemRepository, times(0)).save(any(OrderItem.class));

        verify(productRepository, times(0)).save(any());

        verify(cartService, times(0)).deleteUserCartItems(user);

        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());

        verify(pushNotificationService, times(0)).sendPushNotificationToToken(any(PushNotificationRequest.class));

    }

    @Test
    void listOrders() {

        // Arrange

        User user = new User();

        Order order1 = new Order();

        order1.setId(1L);

        Order order2 = new Order();

        order2.setId(2L);

        List<Order> orders = new ArrayList<>();

        orders.add(order1);

        orders.add(order2);

        when(orderRepository.findByUserOrderByCreateDateDesc(user)).thenReturn(orders);

        // Act

        List<OrderDto> orderDtos = orderService.listOrders(user);

        // Assert

        assertNotNull(orderDtos);

        assertEquals(2, orderDtos.size());

        assertEquals(1L, orderDtos.get(0).getId());

        assertEquals(2L, orderDtos.get(1).getId());

    }

    @Test
    void getOrder_OrderExists() {

        // Arrange

        User user = new User();

        Long orderId = 1L;

        Order order = new Order();

        order.setId(orderId);

        order.setUser(user);

        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(Optional.of(order));

        // Act

        Order retrievedOrder = orderService.getOrder(orderId, user);

        // Assert

        assertNotNull(retrievedOrder);

        assertEquals(orderId, retrievedOrder.getId());

    }

    @Test
    void getOrder_OrderNotFound() {

        // Arrange

        User user = new User();

        Long orderId = 1L;

        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(Optional.empty());

        // Act and Assert

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId, user));

    }

    @Test
    void orderStatus() {

        // Arrange

        OrderItem orderItem = new OrderItem();

        String status = "ORDER_RETURNED";

        // Act

        assertDoesNotThrow(() -> orderService.orderStatus(orderItem, status));

        // Assert

        assertEquals(status, orderItem.getStatus());

    }

    @Test
    void sendDelayChargeEmail() {

        // Arrange

        User user = new User();

        user.setId(1L);

        user.setEmail("xyz@gmail.com");

        Product product = new Product();

        product.setName("product");

        Order order = new Order();

        order.setUser(user);

        OrderItem orderItem = new OrderItem();

        orderItem.setProduct(product);

        orderItem.setOrder(order);

        orderItem.setOwnerId(user.getId());

        orderItem.setRentalStartDate(LocalDateTime.now());

        orderItem.setRentalEndDate(LocalDateTime.now().plusDays(4));

        double securityDeposit = 100.0;

        // Act and Assert

        assertDoesNotThrow(() -> orderService.sendDelayChargeEmail(orderItem, securityDeposit));

    }

    @Test
    void calculateDelayCharge() {

        // Arrange

        LocalDateTime rentalEndDate = LocalDateTime.now().minusDays(5);

        double securityDeposit = 100.0;

        // Act

        double delayCharge = orderService.calculateDelayCharge(rentalEndDate, securityDeposit);

        // Assert

        assertEquals(50.0, delayCharge);

    }

    @Test
    void calculateRemainingDeposit() {

        // Arrange

        OrderItem orderItem = new OrderItem();

        LocalDateTime rentalEndDate = LocalDateTime.now().minusDays(5);

        double securityDeposit = 100.0;

        // Act

        double remainingDeposit = orderService.calculateRemainingDeposit(securityDeposit, rentalEndDate, orderItem);

        // Assert

        assertEquals(50.0, remainingDeposit);

    }

    @Test
    void onClickDashboardYearWiseData() {

        // Create a user

        User user = new User();

        user.setId(5L);

// Create a product associated with the user

        Product product = new Product();

        product.setUser(user);

        product.setId(1L);

        Product product2 = new Product();

        product2.setUser(user);

        product2.setId(1L);

        List<Order> orders = new ArrayList<>();

// Create order 1 with valid product data

        Order order1 = new Order();

        order1.setId(1L);

        OrderItem orderItem1 = new OrderItem();

        orderItem1.setProduct(product); // Set the product associated with the user

        orderItem1.setQuantity(2);

        orderItem1.setPrice(50.0);

        orderItem1.setRentalStartDate(LocalDateTime.now().minusMonths(2));

        orderItem1.setRentalEndDate(LocalDateTime.now().minusMonths(1));

        order1.setOrderItems(Collections.singletonList(orderItem1));

        order1.setUser(user);

        orders.add(order1);

// Create order 2 with valid product data

        Order order2 = new Order();

        order2.setId(2L);

        OrderItem orderItem2 = new OrderItem();

        orderItem2.setProduct(product2); // Set the product associated with the user

        orderItem2.setQuantity(3);

        orderItem2.setPrice(60.0);

        orderItem2.setRentalStartDate(LocalDateTime.now().minusMonths(1));

        orderItem2.setRentalEndDate(LocalDateTime.now());

        order2.setOrderItems(Collections.singletonList(orderItem2));

        order2.setUser(user);

        orders.add(order2);

        // Mock the orderRepository.findAll() to return the orders

        when(orderRepository.findAll()).thenReturn(orders);

        // Act

        Map<Year, Map<YearMonth, Map<String, Object>>> result = orderService.onClickDashboardYearWiseData(user);

        // Assert

        assertNotNull(result);

        assertEquals(1, result.size());

        Year year1 = Year.now();

        assertTrue(result.containsKey(year1));

        Map<YearMonth, Map<String, Object>> year1Data = result.get(year1);

        assertNotNull(year1Data);

        assertEquals(2, year1Data.size());

        Year year2 = Year.now();

        assertTrue(result.containsKey(year2));

        Map<YearMonth, Map<String, Object>> year2Data = result.get(year2);

        assertNotNull(year2Data);

        assertEquals(2, year2Data.size());

    }
}