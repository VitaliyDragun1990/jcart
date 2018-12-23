package com.revenat.jcart.admin.web.controllers.builders;

import com.revenat.jcart.core.entities.OrderItem;
import com.revenat.jcart.core.entities.Product;

import java.math.BigDecimal;

public final class OrderItemBuilder {
    private OrderItem orderItem;

    private OrderItemBuilder() {
        this.orderItem = new OrderItem();
    }

    public static OrderItemBuilder getBuilder() {
        return new OrderItemBuilder();
    }

    public OrderItem build() {
        return this.orderItem;
    }

    public OrderItemBuilder withProduct(Product product) {
        this.orderItem.setProduct(product);
        return this;
    }

    public OrderItemBuilder withPrice(BigDecimal price) {
        this.orderItem.setPrice(price);
        return this;
    }

    public OrderItemBuilder withQuantity(int quantity) {
        this.orderItem.setQuantity(quantity);
        return this;
    }
}
