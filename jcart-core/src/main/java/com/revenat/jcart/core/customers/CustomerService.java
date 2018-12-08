package com.revenat.jcart.core.customers;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;

import java.util.List;

public interface CustomerService {

    Customer getCustomerByEmail(String email);

    Customer createCustomer(Customer customer);

    Customer getCustomerById(Integer id);

    List<Customer> getAllCustomers();

    List<Order> getCustomerOrders(String email);

}
