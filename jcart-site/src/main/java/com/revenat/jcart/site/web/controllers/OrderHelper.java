package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.entities.*;
import com.revenat.jcart.site.web.dto.OrderDTO;
import com.revenat.jcart.site.web.models.Cart;
import com.revenat.jcart.site.web.models.LineItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class OrderHelper {

    private OrderHelper() {}

    static Order buildOrder(OrderDTO orderDTO, Cart cart, Customer customer) {
        Order order = new Order();
        order.setCustomer(customer);

        Address deliveryAddress = buildDeliveryAddress(orderDTO);
        order.setDeliveryAddress(deliveryAddress);

        Address billingAddress = buildBillingAddress(orderDTO);
        order.setBillingAddress(billingAddress);

        Set<OrderItem> orderItems = buildOrderItems(cart);
        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);

        Payment payment = buildPayment(orderDTO);
        order.setPayment(payment);
        return order;
    }

    static Payment buildPayment(OrderDTO orderDTO) {
        Payment payment = new Payment();
        payment.setCcNumber(orderDTO.getCcNumber());
        payment.setCvv(orderDTO.getCvv());
        return payment;
    }

    static Set<OrderItem> buildOrderItems(Cart cart) {
        Set<OrderItem> orderItems = new HashSet<>();
        List<LineItem> lineItems = cart.getItems();
        for (LineItem lineItem : lineItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(lineItem.getProduct());
            orderItem.setQuantity(lineItem.getQuantity());
            orderItem.setPrice(lineItem.getProduct().getPrice());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    static Address buildBillingAddress(OrderDTO orderDTO) {
        Address billingAddress = new Address();
        billingAddress.setAddressLine1(orderDTO.getBillingAddressLine1());
        billingAddress.setAddressLine2(orderDTO.getBillingAddressLine2());
        billingAddress.setCity(orderDTO.getBillingCity());
        billingAddress.setState(orderDTO.getBillingState());
        billingAddress.setZipCode(orderDTO.getBillingZipCode());
        billingAddress.setCountry(orderDTO.getBillingCountry());
        return billingAddress;
    }

    static Address buildDeliveryAddress(OrderDTO orderDTO) {
        Address deliveryAddress = new Address();
        deliveryAddress.setAddressLine1(orderDTO.getAddressLine1());
        deliveryAddress.setAddressLine2(orderDTO.getAddressLine2());
        deliveryAddress.setCity(orderDTO.getCity());
        deliveryAddress.setState(orderDTO.getState());
        deliveryAddress.setZipCode(orderDTO.getZipCode());
        deliveryAddress.setCountry(orderDTO.getCountry());
        return deliveryAddress;
    }

}
