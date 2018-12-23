package com.revenat.jcart.site.web.models;

import com.revenat.jcart.core.entities.Product;

import java.io.Serializable;
import java.math.BigDecimal;

public class LineItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Product product;
    private int quantity;

    public LineItem() {
    }

    public LineItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubTotal() {
        return product.getPrice().multiply(new BigDecimal(quantity));
    }

    public boolean contains(Product product) {
        return this.product.getSku().equals(product.getSku());
    }

    @Override
    public String toString() {
        return "LineItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                '}';
    }
}
