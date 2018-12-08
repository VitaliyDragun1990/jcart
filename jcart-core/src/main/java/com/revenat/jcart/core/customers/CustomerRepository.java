package com.revenat.jcart.core.customers;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Customer findByEmail(String email);

    @Query("SELECT o FROM Order o WHERE o.customer.email=?1")
    List<Order> getCustomerOrders(String email);
}
