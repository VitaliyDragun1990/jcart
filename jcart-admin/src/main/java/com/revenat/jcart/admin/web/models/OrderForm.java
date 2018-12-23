package com.revenat.jcart.admin.web.models;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;
import com.revenat.jcart.core.entities.OrderItem;
import com.revenat.jcart.core.entities.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderForm {

    private String orderNumber;
    private List<OrderItem> items = new ArrayList<>();
    private Customer customer;
    private OrderStatus status;

    public static OrderForm fromOrder(Order order) {
        OrderForm orderForm = null;

        if (order != null) {
            orderForm = new OrderForm();
            orderForm.setCustomer(order.getCustomer());
            orderForm.setItems(new ArrayList<>(order.getItems()));
            orderForm.setOrderNumber(order.getOrderNumber());
            orderForm.setStatus(order.getStatus());
        }

        return orderForm;
    }

    public Order toOrder() {
        Order order = new Order();
        order.setOrderNumber(this.getOrderNumber());
        order.setCustomer(this.getCustomer());
        order.setItems(new HashSet<>(getItems()));
        order.setStatus(this.getStatus());
        return order;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0.0");
        for (OrderItem item : items) {
            amount = amount.add(item.getSubTotal());
        }
        return amount;
    }
}
