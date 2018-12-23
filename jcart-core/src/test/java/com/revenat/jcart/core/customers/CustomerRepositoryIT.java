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
    private static final String USER_EMAIL_WITH_ORDERS = "anna@gmail.com";
    private static final String UNREGISTERED_EMAIL = "dummy@gmail.com";
    private static final String USER_EMAIL_WITHOUT_ORDERS = "jack@gmail.com";

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void findByEmail_RegisteredEmail_CustomerReturned() {

        Customer anna = customerRepository.findByEmail(USER_EMAIL_WITH_ORDERS);

        assertNotNull("Should not be null when searched with registered email.", anna);
        assertThat(anna, Matchers.hasProperty("email", equalTo(USER_EMAIL_WITH_ORDERS)));
    }

    @Test
    public void findByEmail_UnknownEmail_NullReturned() {
        Customer nullCustomer = customerRepository.findByEmail(UNREGISTERED_EMAIL);

        assertNull("Should return null when searched with unregistered email.",nullCustomer);
    }

    @Test
    public void getCustomerOrders_PositiveWhenOrdersExist() {
        List<Order> annaOrders = customerRepository.getCustomerOrders(USER_EMAIL_WITH_ORDERS);

        assertThat("Should return all customer's orders.", annaOrders, hasSize(1));
    }

    @Test
    public void getCustomerOrders_EmptyListWhenNoOrders() {
        List<Order> jackOrders = customerRepository.getCustomerOrders(USER_EMAIL_WITHOUT_ORDERS);

        assertThat("Should be empty for customer with no orders.", jackOrders, empty());
    }
}