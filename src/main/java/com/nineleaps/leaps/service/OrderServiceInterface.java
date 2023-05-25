package com.nineleaps.leaps.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.dto.orders.OrderItemsData;
import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.orders.Order;
import com.nineleaps.leaps.model.orders.OrderItem;

import java.io.FileNotFoundException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface OrderServiceInterface {
    public void placeOrder(User user, String sessionId);

    public List<Order> listOrders(User user);

    public Order getOrder(Long orderId, User user);

    Map<String, Object> dashboard(User user);

    Map<YearMonth, Map<String, Object>> onClickDasboard(User user);

    Map<YearMonth, List<OrderItem>> getOrderedItemsByMonth(User user);

    Map<YearMonth, Map<String, OrderItemsData>> getOrderItemsBySubCategories(User user);

    List<ProductDto> getRentedOutProducts(User user);

    Document getPdf(User user) throws FileNotFoundException, DocumentException;

    void addContent(Document document, User user) throws DocumentException;

    OrderItem getOrderItem(Long orderItemId, User user);

    void orderStatus(OrderItem orderItem, String orderReturned);
}
