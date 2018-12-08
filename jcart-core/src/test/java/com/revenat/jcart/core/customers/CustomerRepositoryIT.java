package com.revenat.jcart.core.customers;

import com.revenat.jcart.core.JCartCoreApplication;
import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration({JCartCoreApplication.class})
public class CustomerRepositoryIT {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void findByEmail_PositiveWhenExists() {
        String email = "anna@gmail.com";

        Customer anna = customerRepository.findByEmail(email);

        assertNotNull(anna);
        assertThat(anna, Matchers.hasProperty("email", equalTo(email)));
    }

    @Test
    public void findByEmail_NullWhenNotExist() {
        String email = "dummy@gmail.com";

        Customer dummy = customerRepository.findByEmail(email);

        assertNull(dummy);
    }

    @Test
    public void getCustomerOrders_PositiveWhenOrdersExist() {
        String email = "anna@gmail.com";

        List<Order> annaOrders = customerRepository.getCustomerOrders(email);

        assertThat(annaOrders, hasSize(1));
    }

    @Test
    public void getCustomerOrders_EmptyListWhenNoOrders() {
        String email = "jack@gmail.com";

        List<Order> jackOrders = customerRepository.getCustomerOrders(email);

        assertThat(jackOrders, empty());
    }
}