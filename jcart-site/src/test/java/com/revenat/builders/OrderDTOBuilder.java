package com.revenat.builders;

import com.revenat.jcart.site.web.dto.OrderDTO;

public class OrderDTOBuilder {

    private OrderDTO order;

    private OrderDTOBuilder() {
        this.order = new OrderDTO();
    }

    public static OrderDTOBuilder getBuilder() {
        return new OrderDTOBuilder();
    }

    public OrderDTOBuilder withFirstName(String firstName) {
        this.order.setFirstName(firstName);
        this.order.setBillingFirstName(firstName);
        return this;
    }

    public OrderDTOBuilder withLastName(String lastName) {
        this.order.setLastName(lastName);
        this.order.setBillingLastName(lastName);
        return this;
    }

    public OrderDTOBuilder withEmail(String email) {
        this.order.setEmailId(email);
        return this;
    }

    public OrderDTOBuilder withPhone(String phone) {
        this.order.setPhone(phone);
        return this;
    }

    public OrderDTOBuilder withAddress(String address) {
        this.order.setAddressLine1(address);
        this.order.setBillingAddressLine1(address);
        return this;
    }

    public OrderDTOBuilder withCity(String city) {
        this.order.setCity(city);
        this.order.setBillingCity(city);
        return this;
    }

    public OrderDTOBuilder withState(String state) {
        this.order.setState(state);
        this.order.setBillingState(state);
        return this;
    }

    public OrderDTOBuilder withZipCode(String zipCode) {
        this.order.setZipCode(zipCode);
        this.order.setBillingZipCode(zipCode);
        return this;
    }

    public OrderDTOBuilder withCountry(String country) {
        this.order.setCountry(country);
        this.order.setBillingCountry(country);
        return this;
    }

    public OrderDTOBuilder withCreditCardNumber(String ccNumber) {
        this.order.setCcNumber(ccNumber);
        return this;
    }

    public OrderDTOBuilder withCVV(String cvv) {
        this.order.setCvv(cvv);
        return this;
    }

    public OrderDTO build() {
        return this.order;
    }
}
