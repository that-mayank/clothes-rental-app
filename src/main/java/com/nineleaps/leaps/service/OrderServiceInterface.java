package com.nineleaps.leaps.service;

import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.dto.orders.OrderDto;
import com.nineleaps.leaps.dto.orders.OrderItemDto;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.orders.OrderReceivedDto;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface OrderServiceInterface {
    void placeOrder(HttpServletRequest request, String razorpayId);

    List<OrderDto> listOrders(HttpServletRequest request);

    Order getOrder(Long orderId, HttpServletRequest request);

    Map<YearMonth, List<OrderReceivedDto>> getOrderedItemsByMonth(HttpServletRequest request);

    Map<YearMonth, Map<String, OrderItemsData>> getOrderItemsBySubCategories(HttpServletRequest request);

    List<ProductDto> getRentedOutProducts(HttpServletRequest request, int pageNumber, int pageSize);

    OrderItem getOrderItem(Long orderItemId, User user);

    void orderStatus(HttpServletRequest request, Long orderItemId, String orderReturned);

    Map<YearMonth, Map<String, OrderItemsData>> getOrderItemsByCategories(HttpServletRequest request);

    Map<Year, Map<YearMonth, Map<String, Object>>> onClickDashboardYearWiseData(HttpServletRequest request);

    Map<YearMonth, List<OrderReceivedDto>> getOrderedItemsByMonthBwDates(HttpServletRequest request, LocalDateTime startDate, LocalDateTime endDate);

    byte[] generateInvoicePDF(List<OrderItem> orderItems, User user, Order order) throws IOException, DocumentException;

    List<OrderItemDto> getOrdersItemByStatus(String shippingStatus, HttpServletRequest request);
}