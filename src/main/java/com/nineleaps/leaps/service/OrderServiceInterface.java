package com.nineleaps.leaps.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface OrderServiceInterface {
    void placeOrder(User user, String sessionId);

    List<OrderDto> listOrders(User user);

    Order getOrder(Long orderId, User user);

    Map<YearMonth, List<OrderReceivedDto>> getOrderedItemsByMonth(User user);

    Map<YearMonth, Map<String, OrderItemsData>> getOrderItemsBySubCategories(User user);

    List<ProductDto> getRentedOutProducts(User user, int pageNumber, int pageSize);

    OrderItem getOrderItem(Long orderItemId, User user);

    void orderStatus(OrderItem orderItem, String orderReturned);

    Map<YearMonth, Map<String, OrderItemsData>> getOrderItemsByCategories(User user);

    Map<Year, Map<YearMonth, Map<String, Object>>> onClickDashboardYearWiseData(User user);

    Map<YearMonth, List<OrderReceivedDto>> getOrderedItemsByMonthBwDates(User user, LocalDateTime startDate, LocalDateTime endDate);

    byte[] generateInvoicePDF(List<OrderItem> orderItems, User user, Order order) throws IOException, DocumentException;

    List<OrderItemDto> getOrdersItemByStatus(String shippingStatus, User user);
}
