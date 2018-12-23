package com.revenat.jcart.admin.web.controllers.builders;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;
import com.revenat.jcart.core.entities.OrderItem;
import com.revenat.jcart.core.entities.OrderStatus;

public final class OrderBuilder {

    private Order order;

    private OrderBuilder() {
        this.order = new Order();
    }

    public static OrderBuilder getBuilder() {
        return new OrderBuilder();
    }

    public Order build() {
        return this.order;
    }

    public OrderBuilder withOrderNumber(String orderNumber) {
        this.order.setOrderNumber(orderNumber);
        return this;
    }

    public OrderBuilder withCustomer(Customer customer) {
        this.order.setCustomer(customer);
        return this;
    }

    public OrderBuilder withOrderItem(OrderItem orderItem) {
        this.order.getItems().add(orderItem);
        return this;
    }

    public OrderBuilder withStatus(OrderStatus status) {
        this.order.setStatus(status);
        return this;
    }
}
