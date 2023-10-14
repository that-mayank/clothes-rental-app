package com.nineleaps.leaps.service.implementation;

import com.itextpdf.text.DocumentException;

import com.nineleaps.leaps.RuntimeBenchmarkExtension;
import com.nineleaps.leaps.dto.cart.CartDto;
import com.nineleaps.leaps.dto.cart.CartItemDto;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.OrderNotFoundException;
import com.nineleaps.leaps.model.Address;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import com.nineleaps.leaps.repository.OrderItemRepository;
import com.nineleaps.leaps.repository.OrderRepository;
import com.nineleaps.leaps.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import static com.nineleaps.leaps.config.MessageStrings.TOTAL_INCOME;
import static com.nineleaps.leaps.config.MessageStrings.TOTAL_NUMBER;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@Tag("unit_tests")
@DisplayName("Order Service Tests")
@ExtendWith(RuntimeBenchmarkExtension.class)
 class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartServiceImpl cartService;

    @Mock
    private EmailServiceImpl emailServiceImpl;

    @Mock
    private PushNotificationServiceImpl pushNotificationService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test Placing an Order")
     void testPlaceOrder() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        String sessionId = "session123";



        // Create a product
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setRentedQuantities(0);
        product.setAvailableQuantities(100);
        ProductUrl productUrl = new ProductUrl(1L,"asdf.jpg",product);
        List<ProductUrl> productUrls = new ArrayList<>();
        productUrls.add(productUrl);
        product.setImageURL((productUrls));
        product.setUser(user);

        // Create a cart item DTO
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setProduct(product);
        cartItemDto.setQuantity(10);
        cartItemDto.setRentalStartDate(LocalDateTime.now());
        cartItemDto.setRentalEndDate(LocalDateTime.now().plusDays(7));

        // Create a cart list and add the cart item DTO
        List<CartItemDto> cartItemDtos = new ArrayList<>();
        cartItemDtos.add(cartItemDto);

        CartDto cartDto = new CartDto();
        cartDto.setCartItems(cartItemDtos);

        // Mock the behavior of cartService and other dependencies
        when(cartService.listCartItems(user)).thenReturn(cartDto);
        // Mock other dependencies as needed...

        // Mock the behavior of orderItemRepository.save()
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem savedOrderItem = invocation.getArgument(0);
            assertNotNull(savedOrderItem);
            assertEquals("Test Product", savedOrderItem.getName());  // Change to match your expected name
            // Add more assertions for orderItem if needed

            return savedOrderItem;
        });

        // Mock the behavior of productRepository.save()
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            assertNotNull(savedProduct);
            assertEquals(10, savedProduct.getRentedQuantities());  // Change to match your expected quantity
            assertEquals(90, savedProduct.getAvailableQuantities());  // Change to match your expected quantity
            // Add more assertions for product if needed

            return savedProduct;
        });

        // Call the method to be tested
        orderService.placeOrder(user, sessionId);
        String message = "Dear null null,\n" +
                "Your Order has been successfully placed.\n" +
                "Here are the details of your order:\n" +
                "Order Id: null\n" +
                "Product: Test Product\n" +
                "Quantity: 10\n" +
                "Price: 7000.0\n" +
                "Total Price of order: 0.0\n\n";



        // Verify method invocations
        verify(cartService).listCartItems(user);
        verify(orderRepository).save(orderCaptor.capture());
        verify(cartService).deleteUserCartItems(user);
        verify(emailServiceImpl).sendEmail("Order Placed", message, user.getEmail());
        verify(pushNotificationService, times(cartItemDtos.size())).sendPushNotificationToToken(any());

        // Add assertions to ensure correctness of order and order items
        Order savedOrder = orderCaptor.getValue();
        assertNotNull(savedOrder);
        assertEquals(cartDto.getTotalCost(), savedOrder.getTotalPrice());
        assertEquals(sessionId, savedOrder.getSessionId());
        assertEquals(user, savedOrder.getUser());

        // Verify the creation of OrderItems and updating product quantities
        for (CartItemDto cartItemDto1 : cartItemDtos) {
            verify(orderItemRepository).save(any(OrderItem.class));

            Product product1 = cartItemDto1.getProduct();
            verify(productRepository).save(product1);
            assertEquals(product1.getRentedQuantities(), product.getRentedQuantities());
            assertEquals(product1.getAvailableQuantities(), product.getAvailableQuantities());
        }
    }


    @Test
    @DisplayName("Test Listing Orders")
     void testListOrders() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);

        // Create a list of orders
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        order1.setId(100L);
        order1.setCreateDate(LocalDateTime.now().minusDays(2));
        order1.setTotalPrice(200.0);
        order1.setSessionId("session1");
        order1.setUser(user);

        Order order2 = new Order();
        order2.setId(101L);
        order2.setCreateDate(LocalDateTime.now().minusDays(1));
        order2.setTotalPrice(300.0);
        order2.setSessionId("session2");
        order2.setUser(user);

        orders.add(order1);
        orders.add(order2);

        // Mock the behavior of orderRepository.findByUserOrderByCreateDateDesc()
        when(orderRepository.findByUserOrderByCreateDateDesc(user)).thenReturn(orders);

        // Call the method to be tested
        List<OrderDto> orderDtos = orderService.listOrders(user);

        // Verify the result
        assertNotNull(orderDtos);
        assertEquals(2, orderDtos.size());

        // Verify the conversion of Order to OrderDto
        for (OrderDto orderDto : orderDtos) {
            Order order = orders.stream()
                    .filter(o -> o.getId().equals(orderDto.getId()))
                    .findFirst()
                    .orElse(null);

            assertNotNull(order);
            assertEquals(order.getId(), orderDto.getId());
            assertEquals(order.getCreateDate(), orderDto.getCreatedDate());
            assertEquals(order.getTotalPrice(), orderDto.getTotalPrice());

        }
    }

    @Test
    @DisplayName("Test Getting an Order - Order Found")
     void testGetOrder_OrderFound() throws OrderNotFoundException {
        // Mock necessary data
        User user = new User();
        user.setId(1L);
        Long orderId = 100L;

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);

        // Mock the behavior of orderRepository.findByIdAndUserId()
        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(Optional.of(order));

        // Call the method to be tested
        Order resultOrder = orderService.getOrder(orderId, user);

        // Verify the result
        assertNotNull(resultOrder);
        assertEquals(orderId, resultOrder.getId());
        assertEquals(user, resultOrder.getUser());
    }

    @Test
    @DisplayName("Test Getting an Order - Order Not Found")
     void testGetOrder_OrderNotFound() {
        // Mock necessary data
        User user = new User();
        user.setId(1L);
        Long orderId = 100L;

        // Mock the behavior of orderRepository.findByIdAndUserId() to return an empty optional
        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(Optional.empty());

        // Call the method to be tested and expect an exception
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId, user));
    }

    @Test
    @DisplayName("Test Order Status - Status Returned")
     void testOrderStatus_StatusReturned() {
        // Mock necessary data
        OrderItem orderItem = new OrderItem();
        orderItem.setId(100L);
        orderItem.setStatus("SomeStatus");  // Any initial status
        int initialAvailableQuantities = 50;
        int initialRentedQuantities = 30;
        orderItem.setQuantity(10);
        Product product = new Product();
        product.setAvailableQuantities(initialAvailableQuantities);
        product.setRentedQuantities(initialRentedQuantities);
        orderItem.setProduct(product);



        // Mock the behavior of orderItemRepository.save()
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem savedOrderItem = invocation.getArgument(0);
            assertNotNull(savedOrderItem);
            assertEquals("ORDER RETURNED", savedOrderItem.getStatus());
            return savedOrderItem;
        });

        // Mock the behavior of productRepository.save()
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            assertNotNull(savedProduct);
            assertEquals(initialAvailableQuantities + 10, savedProduct.getAvailableQuantities());
            assertEquals(Math.max(initialRentedQuantities - 10, 0), savedProduct.getRentedQuantities());
            return savedProduct;
        });

        // Call the method to be tested
        orderService.orderStatus(orderItem, "ORDER RETURNED");

        // Verify the result
        assertEquals("ORDER RETURNED", orderItem.getStatus());
    }

    @Test
    @DisplayName("Test Order Status - Status Not Returned")
     void testOrderStatus_StatusNotReturned() {
        // Mock necessary data
        OrderItem orderItem = new OrderItem();
        orderItem.setId(101L);
        orderItem.setStatus("SomeStatus");  // Any initial status
        orderItem.setQuantity(5);

        // Mock the behavior of orderItemRepository.save()
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem savedOrderItem = invocation.getArgument(0);
            assertNotNull(savedOrderItem);
            assertEquals("SomeStatus", savedOrderItem.getStatus());
            return savedOrderItem;
        });

        // Call the method to be tested
        orderService.orderStatus(orderItem, "SomeStatus");

        // Verify the result
        assertEquals("SomeStatus", orderItem.getStatus());
    }

    @Test
    @DisplayName("Test Send Delay Charge Email")
     void testSendDelayChargeEmail() {
        // Mock necessary data
        OrderItem orderItem = new OrderItem();
        orderItem.setId(100L);
        User user = new User();
        user.setFirstName("John");
        user.setEmail("john@example.com");

        Order order = new Order();
        order.setId(200L);
        order.setUser(user);

        Product product = new Product();
        product.setName("Test Product");



        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setRentalStartDate(LocalDateTime.now());
        orderItem.setRentalEndDate(LocalDateTime.now().plusDays(7));

        double securityDeposit = 100.0;

        // Mock the behavior of emailServiceImpl.sendEmail()
        doNothing().when(emailServiceImpl).sendEmail(anyString(), anyString(), anyString());

        // Call the method to be tested
        orderService.sendDelayChargeEmail(orderItem, securityDeposit);

        // Verify the result
        verify(emailServiceImpl).sendEmail(eq("\"Reminder: Your rental period is ended."), anyString(), eq("john@example.com"));
    }

    @Test
    @DisplayName("Test Calculate Delay Charge - Delay Days Greater Than Zero")
     void testCalculateDelayCharge_DelayDaysGreaterThanZero() {
        // Mock necessary data
        LocalDateTime rentalEndDate = LocalDateTime.now().minusDays(5);  // Set a date 5 days in the past
        double securityDeposit = 100.0;

        // Call the method to be tested
        double delayCharge = orderService.calculateDelayCharge(rentalEndDate, securityDeposit);

        // Verify the result
        double expectedDelayCharge = (securityDeposit * 10.0 / 100) * 5;  // 5 days delay
        assertEquals(expectedDelayCharge, delayCharge, 0.001);  // Use a delta for double comparison
    }

    @Test
    @DisplayName("Test Calculate Delay Charge - Delay Days Zero")
     void testCalculateDelayCharge_DelayDaysZero() {
        // Mock necessary data
        LocalDateTime rentalEndDate = LocalDateTime.now();  // Set rentalEndDate to current date
        double securityDeposit = 100.0;

        // Call the method to be tested
        double delayCharge = orderService.calculateDelayCharge(rentalEndDate, securityDeposit);

        // Verify the result
        assertEquals(0.0, delayCharge, 0.001);  // Use a delta for double comparison
    }

    @Test
    @DisplayName("Test Calculate Remaining Deposit - Delay Days Greater Than Zero")
     void testCalculateRemainingDeposit_DelayDaysGreaterThanZero() {
        // Mock necessary data
        LocalDateTime rentalEndDate = LocalDateTime.now().minusDays(5);  // Set a date 5 days in the past
        double securityDeposit = 100.0;

        OrderItem orderItem = new OrderItem();
        orderItem.setSecurityDeposit(securityDeposit);

        // Mock the behavior of orderItemRepository.save()
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem savedOrderItem = invocation.getArgument(0);
            assertNotNull(savedOrderItem);
            assertTrue(savedOrderItem.getSecurityDeposit() >= 0);
            return savedOrderItem;
        });

        // Call the method to be tested
        double remainingDeposit = orderService.calculateRemainingDeposit(securityDeposit, rentalEndDate, orderItem);

        // Verify the result
        double expectedRemainingDeposit = securityDeposit - ((securityDeposit * 10.0 / 100) * 5);  // 5 days delay
        assertEquals(expectedRemainingDeposit, remainingDeposit, 0.001);  // Use a delta for double comparison
    }

    @Test
    @DisplayName("Test Calculate Remaining Deposit - Delay Days Zero")
     void testCalculateRemainingDeposit_DelayDaysZero() {
        // Mock necessary data
        LocalDateTime rentalEndDate = LocalDateTime.now();  // Set rentalEndDate to current date
        double securityDeposit = 100.0;

        OrderItem orderItem = new OrderItem();
        orderItem.setSecurityDeposit(securityDeposit);

        // Call the method to be tested
        double remainingDeposit = orderService.calculateRemainingDeposit(securityDeposit, rentalEndDate, orderItem);

        // Verify the result
        assertEquals(securityDeposit, remainingDeposit, 0.001);  // Use a delta for double comparison
    }

    @Test
    @DisplayName("Test Calculate Remaining Deposit - Negative Remaining Amount")
      void testCalculateRemainingDeposit_NegativeRemainingAmount() {
         // Mock necessary data
         LocalDateTime rentalEndDate = LocalDateTime.now().minusDays(10);  // Set a date 10 days in the past
         double securityDeposit = 100.0;

         OrderItem orderItem = new OrderItem();
         orderItem.setSecurityDeposit(securityDeposit);

         // Mock the behavior of orderItemRepository.save()
         when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
             OrderItem savedOrderItem = invocation.getArgument(0);
             assertNotNull(savedOrderItem);
             if (savedOrderItem.getSecurityDeposit() >= 0) {
                 // Verify that the security deposit is set correctly
                 assertEquals(0.0, savedOrderItem.getSecurityDeposit(), 0.001);  // Use a delta for double comparison
             } else {
                 fail("Security deposit cannot be negative.");
             }
             return savedOrderItem;
         });

         // Call the method to be tested
         double remainingDeposit = orderService.calculateRemainingDeposit(securityDeposit, rentalEndDate, orderItem);

         // Verify the result
         assertEquals(0.0, remainingDeposit, 0.001);  // Use a delta for double comparison

         // Verify that orderItemRepository.save() was called
         verify(orderItemRepository, times(1)).save(any(OrderItem.class));
     }

    @Test
    @DisplayName("Test On Click Dashboard Year Wise Data")
      void testOnClickDashboardYearWiseData() {
         // Mock necessary data
         User user = new User();

         Order order1 = new Order();
         Order order2 = new Order();

         OrderItem orderItem1 = new OrderItem();
         OrderItem orderItem2 = new OrderItem();

         Product product1 = new Product();
         Product product2 = new Product();

         product1.setUser(user);
         product2.setUser(user);

         orderItem1.setProduct(product1);
         orderItem2.setProduct(product2);

         orderItem1.setRentalStartDate(LocalDateTime.now());
         orderItem1.setRentalEndDate(LocalDateTime.now().plusDays(5));  // 5 days rental duration

         orderItem2.setRentalStartDate(LocalDateTime.now());
         orderItem2.setRentalEndDate(LocalDateTime.now().plusDays(3));  // 3 days rental duration

         orderItem1.setPrice(10.0);
         orderItem2.setPrice(15.0);

         orderItem1.setQuantity(2);
         orderItem2.setQuantity(1);

         order1.setOrderItems(Collections.singletonList(orderItem1));
         order2.setOrderItems(Collections.singletonList(orderItem2));

         // Mock the behavior of orderRepository.findAll()
         when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

         // Call the method to be tested
         Map<Year, Map<YearMonth, Map<String, Object>>> result = orderService.onClickDashboardYearWiseData(user);

         // Verify the result
         assertNotNull(result);
         assertFalse(result.isEmpty());
         // Verify for year 1
         Year year1 = Year.from(orderItem1.getRentalStartDate());
         Map<YearMonth, Map<String, Object>> year1Data = result.get(year1);
         assertNotNull(year1Data);
         assertTrue(year1Data.containsKey(YearMonth.from(orderItem1.getRentalStartDate())));
         Map<String, Object> month1Data = year1Data.get(YearMonth.from(orderItem1.getRentalStartDate()));
         assertEquals(3, month1Data.get(TOTAL_NUMBER)); // Quantity for product 1


         // Verify for year 2
         Year year2 = Year.from(orderItem2.getRentalStartDate());
         Map<YearMonth, Map<String, Object>> year2Data = result.get(year2);
         assertNotNull(year2Data);
         assertTrue(year2Data.containsKey(YearMonth.from(orderItem2.getRentalStartDate())));
         Map<String, Object> month2Data = year2Data.get(YearMonth.from(orderItem2.getRentalStartDate()));
         assertEquals(3, month2Data.get(TOTAL_NUMBER)); // Quantity for product 2
         assertEquals(15.0 * 3+100, month2Data.get(TOTAL_INCOME)); // Earnings for product 2



         // Verify that orderRepository.findAll() was called
         verify(orderRepository, times(1)).findAll();
     }


    @Test
    @DisplayName("Test Get Ordered Items by Month Between Dates")
      void testGetOrderedItemsByMonthBwDates() {
         // Mock necessary data
         User user = new User();
         user.setId(1L);
         user.setEmail("yokes.e@nineleaps.com");
         User user2 = new User();
         user2.setId(2L);
         user2.setEmail("sagar@gmail.com");
         LocalDateTime startDate = LocalDateTime.of(2023, 9, 1, 0, 0);  // September 1, 2023
         LocalDateTime endDate = LocalDateTime.of(2023, 9, 30, 23, 59);  // September 30, 2023

         Order order1 = new Order();
         order1.setUser(user);
         Order order2 = new Order();
         order2.setUser(user);

         OrderItem orderItem1 = new OrderItem();
         OrderItem orderItem2 = new OrderItem();

         Product product1 = new Product();
         Product product2 = new Product();

         product1.setUser(user);
         product2.setUser(user);

         LocalDateTime rentalStartDate1 = LocalDateTime.of(2023, 9, 15, 10, 0);  // September 15, 2023
         LocalDateTime rentalStartDate2 = LocalDateTime.of(2023, 9, 20, 12, 0);  // September 20, 2023

         orderItem1.setProduct(product1);
         orderItem1.setRentalStartDate(rentalStartDate1);
         orderItem1.setImageUrl("/api/asdfgh.jpj");
         orderItem1.setOwnerId(user2.getId());
         orderItem1.setOrder(order1);

         orderItem2.setProduct(product2);
         orderItem2.setRentalStartDate(rentalStartDate2);
         orderItem2.setImageUrl("/api/asdfgh.jpj");
         orderItem2.setOwnerId(user2.getId());
         orderItem2.setOrder(order2);

         order1.setOrderItems(Collections.singletonList(orderItem1));
         order2.setOrderItems(Collections.singletonList(orderItem2));

         // Mock the behavior of orderRepository.findAll()
         when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

         // Call the method to be tested
         Map<YearMonth, List<OrderReceivedDto>> result = orderService.getOrderedItemsByMonthBwDates(user, startDate, endDate);

         // Verify the result
         assertNotNull(result);
         assertFalse(result.isEmpty());

         // Verify that orderRepository.findAll() was called
         verify(orderRepository, times(1)).findAll();
     }

    @Test
    @DisplayName("Test Get Ordered Items by Month")
      void testGetOrderedItemsByMonth() {
         // Mock necessary data
         User user = new User();
         user.setId(1L);
         user.setEmail("yokes.e@nineleaps.com");

         Order order1 = new Order();
         order1.setUser(user);
         Order order2 = new Order();
         order2.setUser(user);

         Product product1 = new Product();
         product1.setUser(user);
         Product product2 = new Product();
         product2.setUser(user);

         LocalDateTime rentalStartDate1 = LocalDateTime.of(2023, 9, 15, 10, 0);  // September 15, 2023
         LocalDateTime rentalStartDate2 = LocalDateTime.of(2023, 9, 20, 12, 0);  // September 20, 2023

         OrderItem orderItem1 = new OrderItem();
         orderItem1.setProduct(product1);
         orderItem1.setRentalStartDate(rentalStartDate1);
         orderItem1.setImageUrl("/api/asdfgh.jpg");
         orderItem1.setOwnerId(2L);
         orderItem1.setOrder(order1);

         OrderItem orderItem2 = new OrderItem();
         orderItem2.setProduct(product2);
         orderItem2.setRentalStartDate(rentalStartDate2);
         orderItem2.setImageUrl("/api/qwerty.jpg");
         orderItem2.setOwnerId(2L);
         orderItem2.setOrder(order2);

         order1.setOrderItems(Collections.singletonList(orderItem1));
         order2.setOrderItems(Collections.singletonList(orderItem2));

         // Mock the behavior of orderRepository.findAll()
         when(orderRepository.findAll()).thenReturn(Arrays.asList(order1,order2));

         // Call the method to be tested
         Map<YearMonth, List<OrderReceivedDto>> result = orderService.getOrderedItemsByMonth(user);

         // Verify the result
         assertNotNull(result);
         assertFalse(result.isEmpty());
         assertEquals(1, result.size());  // Asserting the number of months in the result


         // Verify that orderRepository.findAll() was called
         verify(orderRepository, times(1)).findAll();
     }

    @Test
    @DisplayName("Test Get Order Items by Subcategories")
     void testGetOrderItemsBySubCategories() {
         // Mock necessary data
         User user = new User();
         user.setId(1L);
         user.setEmail("user@example.com");

         SubCategory subCategory = new SubCategory();
         subCategory.setId(1L);
         subCategory.setSubcategoryName("sample subcategory");
            List<SubCategory> subCategoryList = new ArrayList<>();
            subCategoryList.add(subCategory);
         Order order1 = new Order();
         order1.setUser(user);
         Order order2 = new Order();
         order2.setUser(user);


         Product product1 = new Product();
         Product product2 = new Product();

         product1.setUser(user);
         product1.setSubCategories(subCategoryList);
         product2.setUser(user);
         product2.setSubCategories(subCategoryList);

         OrderItem orderItem1 = new OrderItem();
         orderItem1.setProduct(product1);
         orderItem1.setImageUrl("/api/asdfgh.jpg");
         orderItem1.setOwnerId(2L);
         orderItem1.setOrder(order1);

         OrderItem orderItem2 = new OrderItem();
         orderItem2.setProduct(product2);
         orderItem2.setImageUrl("/api/qwerty.jpg");
         orderItem2.setOwnerId(2L);
         orderItem2.setOrder(order2);

         LocalDateTime rentalStartDate1 = LocalDateTime.of(2023, 9, 15, 10, 0);  // September 15, 2023
         LocalDateTime rentalStartDate2 = LocalDateTime.of(2023, 9, 20, 12, 0);  // September 20, 2023

         orderItem1.setProduct(product1);
         orderItem1.setRentalStartDate(rentalStartDate1);

         orderItem2.setProduct(product2);
         orderItem2.setRentalStartDate(rentalStartDate2);

         order1.setOrderItems(Collections.singletonList(orderItem1));
         order2.setOrderItems(Collections.singletonList(orderItem2));

         // Mock the behavior of orderRepository.findAll()
         when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

         // Call the method to be tested
         Map<YearMonth, Map<String, OrderItemsData>> result = orderService.getOrderItemsBySubCategories(user);

         // Verify the result
         assertNotNull(result);
         assertFalse(result.isEmpty());


         // Ensure that the correct subcategory is present in the result
         assertTrue(result.values().stream()
                 .anyMatch(subcategoryMap -> subcategoryMap.containsKey("sample subcategory")));


         // Verify that orderRepository.findAll() was called
         verify(orderRepository, times(1)).findAll();
     }

    @Test
    @DisplayName("Test Get Order Items by Categories")
     void testGetOrderItemsByCategories() {
         // Mock necessary data
         User user = new User();
         user.setId(1L);
         user.setEmail("user@example.com");

         Category category = new Category();
         category.setId(1L);
         category.setCategoryName("sample category");

         List<Category> categoryList = new ArrayList<>();
         categoryList.add(category);

         Order order1 = new Order();
         order1.setUser(user);

         Product product1 = new Product();
         product1.setUser(user);
         product1.setCategories(categoryList);

         OrderItem orderItem1 = new OrderItem();

        // Setting up order items
         orderItem1.setProduct(product1);
         orderItem1.setImageUrl("/api/asdfgh.jpg");
         orderItem1.setOwnerId(1L);
         orderItem1.setOrder(order1);

         LocalDateTime rentalStartDate1 = LocalDateTime.of(2023, 9, 15, 10, 0);  // September 15, 2023

         orderItem1.setProduct(product1);
         orderItem1.setRentalStartDate(rentalStartDate1);

         order1.setOrderItems(Collections.singletonList(orderItem1));

         // Mock the behavior of orderRepository.findAll()
         when(orderRepository.findAll()).thenReturn(List.of(order1));

         // Call the method to be tested
         Map<YearMonth, Map<String, OrderItemsData>> result = orderService.getOrderItemsByCategories(user);

         // Verify the result
         assertNotNull(result);
         assertEquals(1,result.size());

         // Ensure that the correct category is present in the result
         assertTrue(result.values().stream()
                 .anyMatch(categoryMap -> categoryMap.containsKey("sample category")));


         // Verify that orderRepository.findAll() was called
         verify(orderRepository, times(1)).findAll();
     }

    @Test
    @DisplayName("Test Get Rented Out Products")
      void testGetRentedOutProducts() {
         // Mock necessary data
         User user = new User();
         user.setId(1L);

         // Mock OrderItem and Product
         OrderItem orderItem = new OrderItem();
         Product product = new Product();
         orderItem.setProduct(product);

         // Mock Page and Pageable
         Page<OrderItem> orderItemPage = new PageImpl<>(Collections.singletonList(orderItem));
         Pageable pageable = PageRequest.of(0, 10);

         // Mock the behavior of orderItemRepository.findByOwnerId
         when(orderItemRepository.findByOwnerId(pageable, user.getId())).thenReturn(orderItemPage);

         // Call the method to be tested
         List<ProductDto> productDtoList = orderService.getRentedOutProducts(user, 0, 10);

         // Verify the result
         assertNotNull(productDtoList);
         assertEquals(1, productDtoList.size());

         // Ensure the ProductDto is correctly created from OrderItem
         ProductDto productDto = productDtoList.get(0);
         assertEquals(product.getId(), productDto.getId());

         // Verify that orderItemRepository.findByOwnerId was called
         verify(orderItemRepository, times(1)).findByOwnerId(pageable, user.getId());
     }

    @Test
    @DisplayName("Test Get Order Item")
      void testGetOrderItem() {
         // Mock necessary data
         User user = new User();
         user.setId(1L);

         // Create a sample order item
         OrderItem orderItem = new OrderItem();
         orderItem.setId(100L);
         Order order = new Order();
         order.setUser(user);
         orderItem.setOrder(order);

         // Mock the behavior of orderItemRepository.findById
         when(orderItemRepository.findById(100L)).thenReturn(Optional.of(orderItem));

         // Call the method to be tested
         OrderItem resultOrderItem = orderService.getOrderItem(100L, user);

         // Verify the result when the order item is found and associated with the user
         assertNotNull(resultOrderItem);
         assertEquals(orderItem, resultOrderItem);

         // Call the method with a different order item ID
         OrderItem nonMatchingResult = orderService.getOrderItem(200L, user);

         // Verify that the order item is not found and null is returned
         assertNull(nonMatchingResult);

         // Call the method with a different user
         User differentUser = new User();
         differentUser.setId(2L);
         OrderItem resultForDifferentUser = orderService.getOrderItem(100L, differentUser);

         // Verify that the order item is not associated with the different user and null is returned
         assertNull(resultForDifferentUser);

         // Verify that orderItemRepository.findById was called
         verify(orderItemRepository, times(3)).findById(anyLong());
     }

    @Test
    @DisplayName("Test Get Rental Periods")
      void testGetRentalPeriods() {
         // Mock necessary data
         User user = new User();
         user.setEmail("user@example.com");
         user.setFirstName("John");

         OrderItem orderItem1 = new OrderItem();

         Order order1 = new Order();
         order1.setUser(user);
         orderItem1.setOrder(order1);

         LocalDateTime rentalStartDate1 = LocalDateTime.now().minusDays(3);
         LocalDateTime rentalEndDate1 = LocalDateTime.now().minusDays(1);
         orderItem1.setRentalStartDate(rentalStartDate1);
         orderItem1.setRentalEndDate(rentalEndDate1);

         // Mock the behavior of orderItemRepository.findAll()
         when(orderItemRepository.findAll()).thenReturn(List.of(orderItem1));

         // Call the method to be tested
         orderService.getRentalPeriods();


         // Verify that the correct emails were sent
         verify(emailServiceImpl).sendEmail(eq("Reminder: Your rental period is ending soon"), anyString(), eq(user.getEmail()));

         // Verify that orderItemRepository.findAll() was called
         verify(orderItemRepository, times(1)).findAll();
     }

    @Test
    @DisplayName("Test Generate Invoice PDF")
      void testGenerateInvoicePDF() throws DocumentException {
         // Mock necessary data
         User user = new User();
         user.setFirstName("John");
         user.setLastName("Doe");
         Address address = new Address();
         address.setAddressLine1("sample addressline 1");
         address.setAddressLine2("sample address line 2");

         List<Address> addressList = new ArrayList<>();
         addressList.add(address);
         user.setAddresses(addressList);

         Order order = new Order();
         order.setId(1L);
         order.setUser(user);
         order.setCreateDate(LocalDateTime.of(2023, 9, 25, 10, 30));
         order.setTotalPrice(150.0);

         List<OrderItem> orderItems = Arrays.asList(
                 createOrderItem("Product 1", 2, 30.0, "Brand A", LocalDateTime.of(2023, 9, 15, 8, 0), LocalDateTime.of(2023, 9, 18, 8, 0), 50.0),
                 createOrderItem("Product 2", 1, 50.0, "Brand B", LocalDateTime.of(2023, 9, 20, 10, 0), LocalDateTime.of(2023, 9, 23, 10, 0), 70.0)
         );

         byte[] pdfBytes;
         pdfBytes = orderService.generateInvoicePDF(orderItems, user, order);

         assertNotNull(pdfBytes);
         assertTrue(pdfBytes.length>0);


     }

     // Helper method to create an order item
     private OrderItem createOrderItem(String name, int quantity, double price, String brand, LocalDateTime rentalStartDate, LocalDateTime rentalEndDate, double securityDeposit) {
         OrderItem orderItem = new OrderItem();
         Product product = new Product();
         product.setBrand(brand);

         orderItem.setName(name);
         orderItem.setQuantity(quantity);
         orderItem.setPrice(price);
         orderItem.setProduct(product);
         orderItem.setRentalStartDate(rentalStartDate);
         orderItem.setRentalEndDate(rentalEndDate);
         orderItem.setSecurityDeposit(securityDeposit);

         return orderItem;
     }

    @Test
    @DisplayName("Test Get Orders Item by Status")
     void testGetOrdersItemByStatus() {
         // Mock necessary data
         User user = new User();
         user.setId(1L);
         user.setEmail("user@example.com");

         Product product1 = new Product();
         Product product2 = new Product();

         product1.setUser(user);
         product2.setUser(user);

         OrderItem orderItem1 = new OrderItem();
         OrderItem orderItem2 = new OrderItem();
         OrderItem orderItem3 = new OrderItem();

         orderItem1.setProduct(product1);
         orderItem1.setStatus("SHIPPED");
         orderItem1.setId(1L);

         orderItem2.setProduct(product2);
         orderItem2.setStatus("DELIVERED");
         orderItem2.setId(2L);

         orderItem3.setProduct(product1);
         orderItem3.setStatus("SHIPPED");
         orderItem3.setId(3L);

         // Mock the behavior of orderItemRepository.findAll()
         when(orderItemRepository.findAll()).thenReturn(Arrays.asList(orderItem1, orderItem2, orderItem3));

         // Call the method to be tested
         List<OrderItemDto> result = orderService.getOrdersItemByStatus("SHIPPED", user);

         // Verify the result
         assertNotNull(result);
         assertEquals(2, result.size());

         // Assert that the returned order items match the expected ones
         assertTrue(result.stream().anyMatch(dto -> orderItem1.getId() != null && dto.getId() != null && dto.getId().equals(orderItem1.getId())));
         assertTrue(result.stream().anyMatch(dto -> orderItem3.getId() != null && dto.getId() != null && dto.getId().equals(orderItem3.getId())));

         // Verify that orderItemRepository.findAll() was called
         verify(orderItemRepository, times(1)).findAll();
     }


 }
