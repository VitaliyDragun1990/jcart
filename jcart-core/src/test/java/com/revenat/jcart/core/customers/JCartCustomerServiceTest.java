package com.revenat.jcart.core.customers;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

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
    private static final Integer CUSTOMER_ID = 1;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private JCartCustomerService customerService;

    @Test
    public void getCustomerByEmail_ShouldInteractWithRepository() {
        when(customerRepository.findByEmail(anyString())).thenReturn(DUMMY_CUSTOMER);

        Customer customer = customerService.getCustomerByEmail(DUMMY_EMAIL);

        assertSame(DUMMY_CUSTOMER, customer);
        verify(customerRepository, times(1)).findByEmail(anyString());
    }

    @Test
    public void createCustomer_ShouldInteractWithRepository() {
        when(customerRepository.save(any(Customer.class))).thenAnswer(
                (Answer<Customer>) invocationOnMock -> (Customer) invocationOnMock.getArguments()[0]
        );

        Customer savedCustomer = customerService.createCustomer(DUMMY_CUSTOMER);

        assertSame(DUMMY_CUSTOMER, savedCustomer);
        verify(customerRepository, times(1)).save(DUMMY_CUSTOMER);
    }

    @Test
    public void getCustomerById_ShouldInteractWithRepository() {
        when(customerRepository.findOne(CUSTOMER_ID)).thenReturn(DUMMY_CUSTOMER);

        Customer customer = customerService.getCustomerById(CUSTOMER_ID);

        assertSame(DUMMY_CUSTOMER, customer);
        verify(customerRepository, times(1)).findOne(CUSTOMER_ID);
    }

    @Test
    public void getAllCustomers_ShouldInteractWithRepository() {
        List<Customer> allCustomers = new ArrayList<Customer>(){{add(DUMMY_CUSTOMER);}};
        when(customerRepository.findAll()).thenReturn(allCustomers);

        List<Customer> customers = customerService.getAllCustomers();

        assertSame(allCustomers, customers);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    public void getCustomerOrders_ShouldInteractWithRepository() {
        List<Order> customerOrders = new ArrayList<Order>(){{add(new Order());}};
        when(customerRepository.getCustomerOrders(DUMMY_EMAIL)).thenReturn(customerOrders);

        List<Order> orders = customerService.getCustomerOrders(DUMMY_EMAIL);

        assertThat(orders, hasSize(1));
        verify(customerRepository, times(1)).getCustomerOrders(DUMMY_EMAIL);
    }
}