package com.revenat.builders;

import com.revenat.jcart.core.entities.Customer;

public final class CustomerBuilder {

    private Customer customer;

    public static CustomerBuilder getBuilder() {
        return new CustomerBuilder();
    }

    private CustomerBuilder() {
        this.customer = new Customer();
    }

    public CustomerBuilder withId(Integer id) {
        this.customer.setId(id);
        return this;
    }

    public CustomerBuilder withFirstName(String firstName) {
        this.customer.setFirstName(firstName);
        return this;
    }

    public CustomerBuilder withLastName(String lastName) {
        this.customer.setLastName(lastName);
        return this;
    }

    public CustomerBuilder withEmail(String email) {
        this.customer.setEmail(email);
        return this;
    }

    public CustomerBuilder withPassword(String password) {
        this.customer.setPassword(password);
        return this;
    }

    public CustomerBuilder withPhone(String phone) {
        this.customer.setPhone(phone);
        return this;
    }

    public Customer build() {
        return this.customer;
    }
}
