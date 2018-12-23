package com.revenat.jcart.site.web.models;

import com.revenat.jcart.core.entities.Address;
import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Payment;
import com.revenat.jcart.core.entities.Product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<LineItem> items;
    private Customer customer;
    private Address deliveryAddress;
    private Payment payment;

    public Cart() {
        items = new ArrayList<>();
        customer = new Customer();
        deliveryAddress = new Address();
        payment = new Payment();
    }

    public void addItem(Product product) {
        for (LineItem lineItem : items) {
            if (lineItem.contains(product)) {
                lineItem.setQuantity(lineItem.getQuantity()+1);
                return;
            }
        }
        LineItem item = new LineItem(product, 1);
        this.items.add(item);
    }

    public void updateItemQuantity(Product product, int quantity) {
        for (LineItem lineItem : items) {
            if (lineItem.contains(product)) {
                lineItem.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeItem(Product product) {
        items.removeIf(item -> item.contains(product));
    }

    public void removeItem(String sku) {
        Product product = new Product();
        product.setSku(sku);
        removeItem(product);
    }

    public void clearItems() {
        items = new ArrayList<>();
    }

    public int getItemCount() {
        return items.stream().map(LineItem::getQuantity).mapToInt(Integer::intValue).sum();
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(LineItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                ", customer=" + customer +
                ", deliveryAddress=" + deliveryAddress +
                ", payment=" + payment +
                '}';
    }
}
