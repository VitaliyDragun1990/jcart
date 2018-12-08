package com.revenat.jcart.core.customers;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JCartCustomerServiceTest {
    private static final Customer DUMMY_CUSTOMER = new Customer();
    private static final String DUMMY_EMAIL = "dummy@gmail.com";

    @Mock
    private CustomerRepository mockRepository;

    @InjectMocks
    private JCartCustomerService customerService;

    @Test
    public void testGetCustomerByEmail() {
        when(mockRepository.findByEmail(anyString())).thenReturn(DUMMY_CUSTOMER);

        Customer customer = customerService.getCustomerByEmail(DUMMY_EMAIL);

        assertSame(DUMMY_CUSTOMER, customer);
        verify(mockRepository, times(1)).findByEmail(anyString());
    }

    @Test
    public void testCreateCustomer() {
        when(mockRepository.save(any(Customer.class))).thenReturn(DUMMY_CUSTOMER);

        Customer savedCustomer = customerService.createCustomer(DUMMY_CUSTOMER);

        assertSame(DUMMY_CUSTOMER, savedCustomer);
        verify(mockRepository, times(1)).save(DUMMY_CUSTOMER);
    }

    @Test
    public void testGetCustomerById() {
        Integer customerId = 1;
        when(mockRepository.findOne(customerId)).thenReturn(DUMMY_CUSTOMER);

        Customer customer = customerService.getCustomerById(customerId);

        assertSame(DUMMY_CUSTOMER, customer);
        verify(mockRepository, times(1)).findOne(customerId);
    }

    @Test
    public void testGetAllCustomers() {
        List<Customer> allCustomers = new ArrayList<Customer>(){{add(DUMMY_CUSTOMER);}};
        when(mockRepository.findAll()).thenReturn(allCustomers);

        List<Customer> customers = customerService.getAllCustomers();

        assertSame(allCustomers, customers);
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    public void testGetCustomerOrders() {
        List<Order> customerOrders = new ArrayList<Order>(){{add(new Order());}};
        when(mockRepository.getCustomerOrders(DUMMY_EMAIL)).thenReturn(customerOrders);

        List<Order> orders = customerService.getCustomerOrders(DUMMY_EMAIL);

        assertThat(orders, hasSize(1));
        verify(mockRepository, times(1)).getCustomerOrders(DUMMY_EMAIL);
    }
}