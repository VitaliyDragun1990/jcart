package com.revenat.jcart.core.orders;

import com.revenat.jcart.core.entities.Order;

import java.util.List;

public interface OrderService {

    /**
     * Creates new {@link Order} instance using data from given {@link Order} instance.
     * @param order {@link Order} instance which data is used to create a new {@link Order} instance.
     * @return newly created {@link Order} instance.
     */
    Order createOrder(Order order);

    /**
     * Retrieves {@link Order} instance using a given order number for searching.
     * @param orderNumber order number look for a {@link Order} instance.
     * @return Found {@link Order} instance if any, or {@code null}.
     */
    Order getOrder(String orderNumber);

    /**
     * Gets a list of all {@link Order}s available.
     * @return list of all available {@link Order}s.
     */
    List<Order> getAllOrders();

    /**
     * Updates {@link Order} instance in data store using data from given instance.
     * @param order {@link Order} instance to be updated.
     * @return updated {@link Order} instance.
     */
    Order updateOrder(Order order);
}
