package com.revenat.jcart.site.web.controllers;

import com.revenat.builders.OrderDTOBuilder;
import com.revenat.builders.ProductBuilder;
import com.revenat.jcart.core.entities.*;
import com.revenat.jcart.site.web.dto.OrderDTO;
import com.revenat.jcart.site.web.models.Cart;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class OrderHelperTest {

    private static final String CREDIT_CARD_NUMBER = "Credit Card Number";
    private static final String CVV = "CVV";
    private static final String PRODUCT_NAME = "Product name";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.ONE;
    private static final String ADDRESS_LINE = "Address Line";
    private static final String CITY = "City";
    private static final String STATE = "State";
    private static final String ZIP_CODE = "Zip Code";
    private static final String COUNTRY = "Country";
    private static final String CUSTOMER_EMAIL = "jack@gmail.com";

    @Test
    public void buildDeliveryAddress_OrderDTOGiven_ShouldReturnDeliveryAddress() {
        OrderDTO orderDTO = OrderDTOBuilder.getBuilder()
                .withAddress(ADDRESS_LINE)
                .withCity(CITY)
                .withState(STATE)
                .withZipCode(ZIP_CODE)
                .withCountry(COUNTRY)
                .build();

        Address deliveryAddress = OrderHelper.buildDeliveryAddress(orderDTO);

        assertAddress(deliveryAddress);
    }

    @Test
    public void buildBillingAddress_OrderDTOGiven_ShouldReturnBillingAddress() {
        OrderDTO orderDTO = OrderDTOBuilder.getBuilder()
                .withAddress(ADDRESS_LINE)
                .withCity(CITY)
                .withState(STATE)
                .withZipCode(ZIP_CODE)
                .withCountry(COUNTRY)
                .build();

        Address billingAddress = OrderHelper.buildBillingAddress(orderDTO);

        assertAddress(billingAddress);
    }

    @Test
    public void buildPayment_OrderDTOGiven_ShouldReturnPayment() {
        OrderDTO orderDTO = OrderDTOBuilder.getBuilder().withCreditCardNumber(CREDIT_CARD_NUMBER).withCVV(CVV).build();

        Payment payment = OrderHelper.buildPayment(orderDTO);

        assertPayment(payment);
    }

    @Test
    public void buildOrderItems_CartGiven_ShouldReturnOrderItems() {
        Cart cart = new Cart();
        Product product = new ProductBuilder().withName(PRODUCT_NAME).withPrice(PRODUCT_PRICE).build();
        cart.addItem(product);

        Set<OrderItem> orderItems = OrderHelper.buildOrderItems(cart);

        assertOrderItems(orderItems);
    }

    @Test
    public void buildOrder_ShouldBuildOrderInstance() {
        OrderDTO orderDTO = OrderDTOBuilder.getBuilder()
                .withAddress(ADDRESS_LINE)
                .withCity(CITY)
                .withState(STATE)
                .withZipCode(ZIP_CODE)
                .withCountry(COUNTRY)
                .withCreditCardNumber(CREDIT_CARD_NUMBER)
                .withCVV(CVV)
                .build();
        Cart cart = new Cart();
        Product product = new ProductBuilder().withName(PRODUCT_NAME).withPrice(PRODUCT_PRICE).build();
        cart.addItem(product);
        Customer customer = new Customer();
        customer.setEmail(CUSTOMER_EMAIL);

        Order order = OrderHelper.buildOrder(orderDTO, cart, customer);

        assertAddress(order.getDeliveryAddress());
        assertAddress(order.getBillingAddress());
        assertPayment(order.getPayment());
        assertOrderItems(order.getItems());
        assertThat(order.getCustomer(), hasProperty("email", is(CUSTOMER_EMAIL)));
    }

    private void assertAddress(Address address) {
        assertThat(address.getAddressLine1(), is(ADDRESS_LINE));
        assertThat(address.getCity(), is(CITY));
        assertThat(address.getState(), is(STATE));
        assertThat(address.getZipCode(), is(ZIP_CODE));
        assertThat(address.getCountry(), is(COUNTRY));
    }

    private void assertOrderItems(Set<OrderItem> orderItems) {
        assertThat(orderItems, hasSize(1));
        OrderItem orderItem = orderItems.iterator().next();
        assertThat(orderItem, hasProperty("product", allOf(
                hasProperty("name", is(PRODUCT_NAME)),
                hasProperty("price", is(PRODUCT_PRICE))
        )));
    }

    private void assertPayment(Payment payment) {
        assertThat(payment.getCcNumber(), is(CREDIT_CARD_NUMBER));
        assertThat(payment.getCvv(), is(CVV));
    }
}