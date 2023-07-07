package com.nineleaps.leaps.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.AuthenticationFailException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;
import com.nineleaps.leaps.service.OrderServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderController.class)
class OrderControllerTest {

    @Mock
    private OrderServiceInterface orderService;

    @Mock
    private Helper helper;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(orderService, helper);
    }

    @Test
    void placeOrder_ValidRazorpayId_ReturnsCreatedResponse() throws AuthenticationFailException {
        // Arrange
        String razorpayId = "razorpayId";
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        doNothing().when(orderService).placeOrder(user, razorpayId);

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.placeOrder(razorpayId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Order has been placed", response.getMessage());

        verify(orderService).placeOrder(user, razorpayId);
    }

    @Test
    void getAllOrders_ValidToken_ReturnsListOfOrders() throws AuthenticationFailException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Order order = new Order();
        order.setUser(user);
        List<OrderDto> orders = Arrays.asList(new OrderDto(order), new OrderDto(order));
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.listOrders(user)).thenReturn(orders);

        // Act
        ResponseEntity<List<OrderDto>> responseEntity = orderController.getAllOrders(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<OrderDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(orders.size(), responseBody.size());

        verify(orderService).listOrders(user);
    }

    @Test
    void getOrderById_ValidOrderIdAndToken_ReturnsOrderDetails() throws AuthenticationFailException {
        // Arrange
        Long orderId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Order order = new Order();
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getOrder(orderId, user)).thenReturn(order);

        // Act
        ResponseEntity<Order> responseEntity = orderController.getOrderById(orderId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Order responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(order, responseBody);

        verify(orderService).getOrder(orderId, user);
    }

    @Test
    void orderInTransit_ValidOrderItemIdAndToken_ReturnsSuccessResponse() throws AuthenticationFailException {
        // Arrange
        Long orderItemId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        OrderItem orderItem = new OrderItem();
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getOrderItem(orderItemId, user)).thenReturn(orderItem);
        doNothing().when(orderService).orderStatus(orderItem, "IN TRANSIT");

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.orderInTransit(orderItemId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Order is in transit", response.getMessage());

        verify(orderService).orderStatus(orderItem, "IN TRANSIT");
    }

    @Test
    void orderDelivered_ValidOrderItemIdAndToken_ReturnsSuccessResponse() throws AuthenticationFailException {
        // Arrange
        Long orderItemId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        OrderItem orderItem = new OrderItem();

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getOrderItem(orderItemId, user)).thenReturn(orderItem);
        doNothing().when(orderService).orderStatus(orderItem, "DELIVERED");

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.orderDelivered(orderItemId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Order delivered", response.getMessage());

        verify(orderService).orderStatus(orderItem, "DELIVERED");
    }

    @Test
    void orderPickup_ValidOrderItemIdAndToken_ReturnsSuccessResponse() throws AuthenticationFailException {
        // Arrange
        Long orderItemId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        OrderItem orderItem = new OrderItem();
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getOrderItem(orderItemId, user)).thenReturn(orderItem);
        doNothing().when(orderService).orderStatus(orderItem, "PICKED UP");

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.orderPickup(orderItemId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Order is picked up", response.getMessage());

        verify(orderService).orderStatus(orderItem, "PICKED UP");
    }

    @Test
    void orderReturned_ValidOrderItemIdAndToken_ReturnsSuccessResponse() throws AuthenticationFailException {
        // Arrange
        Long orderItemId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        OrderItem orderItem = new OrderItem();
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getOrderItem(orderItemId, user)).thenReturn(orderItem);
        doNothing().when(orderService).orderStatus(orderItem, "ORDER RETURNED");

        // Act
        ResponseEntity<ApiResponse> responseEntity = orderController.orderReturned(orderItemId, request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Order is returned", response.getMessage());

        verify(orderService).orderStatus(orderItem, "ORDER RETURNED");
    }

    @Test
    void dashboard_ValidToken_ReturnsDashboardData() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Map<String, Object> dashboardData = Map.of("key", "value");
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.dashboard(user)).thenReturn(dashboardData);

        // Act
        ResponseEntity<Map<String, Object>> responseEntity = orderController.dashboard(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, Object> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(dashboardData, responseBody);

        verify(orderService).dashboard(user);
    }

    @Test
    void onClickDashboard_ValidToken_ReturnsDashboardData() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Map<YearMonth, Map<String, Object>> dashboardData = Map.of(YearMonth.now(), Map.of("key", "value"));
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.onClickDasboard(user)).thenReturn(dashboardData);

        // Act
        ResponseEntity<Map<YearMonth, Map<String, Object>>> responseEntity = orderController.onClickDashboard(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<YearMonth, Map<String, Object>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(dashboardData, responseBody);

        verify(orderService).onClickDasboard(user);
    }

    @Test
    void onClickDashboardYearWiseData_ValidToken_ReturnsYearWiseDashboardData() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Map<Year, Map<YearMonth, Map<String, Object>>> dashboardData = Map.of(Year.now(), Map.of(YearMonth.now(), Map.of("key", "value")));
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.onClickDashboardYearWiseData(user)).thenReturn(dashboardData);

        // Act
        ResponseEntity<Map<Year, Map<YearMonth, Map<String, Object>>>> responseEntity = orderController.onClickDashboardYearWiseData(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<Year, Map<YearMonth, Map<String, Object>>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(dashboardData, responseBody);

        verify(orderService).onClickDashboardYearWiseData(user);
    }

    @Test
    void getOrderItemsDashboard_ValidToken_ReturnsOrderItemsDashboardData() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Order order = new Order();
        order.setUser(user);
        Product product = new Product();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setImageUrl("https://e651-106-51-70-135.ngrok-free.app/api/v1/file/view/test_image.png");
        orderItem.setRentalStartDate(LocalDateTime.now().minusDays(2));
        orderItem.setRentalEndDate(LocalDateTime.now().plusDays(2));
        Map<YearMonth, List<OrderReceivedDto>> orderItemsDashboardData = Map.of(YearMonth.now(), Arrays.asList(new OrderReceivedDto(orderItem)));
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getOrderedItemsByMonth(user)).thenReturn(orderItemsDashboardData);

        // Act
        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = orderController.getOrderItemsDashboard(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<YearMonth, List<OrderReceivedDto>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(orderItemsDashboardData, responseBody);

        verify(orderService).getOrderedItemsByMonth(user);
    }

    @Test
    void getOrderItemsBySubCategories_ValidToken_ReturnsOrderItemsBySubCategoriesData() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Map<YearMonth, Map<String, OrderItemsData>> orderItemsBySubCategoriesData = Map.of(YearMonth.now(), Map.of("category", new OrderItemsData()));
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getOrderItemsBySubCategories(user)).thenReturn(orderItemsBySubCategoriesData);

        // Act
        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = orderController.getOrderItemsBySubCategories(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<YearMonth, Map<String, OrderItemsData>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(orderItemsBySubCategoriesData, responseBody);

        verify(orderService).getOrderItemsBySubCategories(user);
    }

    @Test
    void getOrderItemsByCategories_ValidToken_ReturnsOrderItemsByCategoriesData() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Map<YearMonth, Map<String, OrderItemsData>> orderItemsByCategoriesData = Map.of(YearMonth.now(), Map.of("category", new OrderItemsData()));
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getOrderItemsByCategories(user)).thenReturn(orderItemsByCategoriesData);

        // Act
        ResponseEntity<Map<YearMonth, Map<String, OrderItemsData>>> responseEntity = orderController.getOrderItemsByCategories(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<YearMonth, Map<String, OrderItemsData>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(orderItemsByCategoriesData, responseBody);

        verify(orderService).getOrderItemsByCategories(user);
    }

    @Test
    void getRentedOutProducts_ValidToken_ReturnsRentedOutProducts() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        List<ProductDto> rentedOutProducts = Arrays.asList(new ProductDto());
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getRentedOutProducts(user)).thenReturn(rentedOutProducts);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = orderController.getRentedOutProducts(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<ProductDto> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(rentedOutProducts, responseBody);

        verify(orderService).getRentedOutProducts(user);
    }


    @Test
    void getPdf_ValidToken_ReturnsPdfFile() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Document document = mock(Document.class);
        ByteArrayOutputStream baos = mock(ByteArrayOutputStream.class);
        byte[] pdfBytes = "pdf content".getBytes();
        InputStreamResource expectedInputStreamResource = createInputStreamResource(pdfBytes);
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getPdf(user)).thenReturn(document);

        // Mock the document closing process
        doNothing().when(document).close();

        // Mock the PdfWriter behavior
        PdfWriter pdfWriter = mock(PdfWriter.class);
        doAnswer(invocation -> {
            PdfWriter writer = invocation.getArgument(0);
            writer.getDirectContent().getPdfDocument().close();
            return null;
        }).when(pdfWriter).close();
        doNothing().when(document).open();

        // Mock the PdfWriter creation
        whenNew(PdfWriter.class).withAnyArguments().thenReturn(pdfWriter);

        // Mock the toByteArray() method
        when(baos.toByteArray()).thenReturn(pdfBytes);


        // Mock the add() method
        doAnswer(invocation -> {
            Object arg = invocation.getArgument(0);
            // Handle the behavior of adding the object to the document
            // For example: document.add(arg);
            return null;
        }).when(document).add(any());

        // Act
        ResponseEntity<InputStreamResource> responseEntity = orderController.getPdf(request);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        InputStreamResource responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
//        assertInputStreamContentEquals(expectedInputStreamResource.getInputStream(), responseBody.getInputStream());

        HttpHeaders responseHeaders = responseEntity.getHeaders();
        assertNotNull(responseHeaders);
        assertEquals(MediaType.APPLICATION_PDF, responseHeaders.getContentType());
//        assertEquals("form-data; name=attachment; filename=report.pdf", responseHeaders.getContentDisposition().toString());

        verify(orderService).getPdf(user);
        verify(document).close();
//        verify(pdfWriter).close();
    }

    private InputStreamResource createInputStreamResource(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return new InputStreamResource(inputStream);
    }

    private void assertInputStreamContentEquals(InputStream expected, InputStream actual) throws IOException {
        try (BufferedReader expectedReader = new BufferedReader(new InputStreamReader(expected, StandardCharsets.UTF_8));
             BufferedReader actualReader = new BufferedReader(new InputStreamReader(actual, StandardCharsets.UTF_8))) {
            String expectedLine;
            String actualLine;
            while ((expectedLine = expectedReader.readLine()) != null) {
                actualLine = actualReader.readLine();
                assertEquals(expectedLine, actualLine);
            }
            assertNull(actualReader.readLine());
        }
    }

    @Test
    void getOrderItemsDashboardBwDates_ValidTokenAndDates_ReturnsOrderItemsDashboardDataBetweenDates() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        String authorizationHeader = "Bearer token";
        User user = new User();
        Order order = new Order();
        order.setUser(user);
        Product product = new Product();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setImageUrl("https://e651-106-51-70-135.ngrok-free.app/api/v1/file/view/test_image.png");
        orderItem.setRentalStartDate(LocalDateTime.now().minusDays(2));
        orderItem.setRentalEndDate(LocalDateTime.now().plusDays(2));
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Map<YearMonth, List<OrderReceivedDto>> orderItemsDashboardData = Map.of(YearMonth.now(), Arrays.asList(new OrderReceivedDto(orderItem)));
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(helper.getUser("token")).thenReturn(user);
        when(orderService.getOrderedItemsByMonthBwDates(user, startDate, endDate)).thenReturn(orderItemsDashboardData);

        // Act
        ResponseEntity<Map<YearMonth, List<OrderReceivedDto>>> responseEntity = orderController.getOrderItemsDashboardBwDates(request, startDate, endDate);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<YearMonth, List<OrderReceivedDto>> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(orderItemsDashboardData, responseBody);

        verify(orderService).getOrderedItemsByMonthBwDates(user, startDate, endDate);
    }

}