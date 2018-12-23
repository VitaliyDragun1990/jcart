package com.revenat.jcart.site.security;

import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    private static final String CUSTOMER_PASSWORD = "test";
    private static final String CUSTOMER_EMAIL = "john@gmail.com";

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    public void loadUserByUsername_RegisteredEmailGiven_ReturnsRegisteredUser() {
        Customer customer = customerWithEmailAndPassword(CUSTOMER_EMAIL, CUSTOMER_PASSWORD);
        when(customerService.getCustomerByEmail(CUSTOMER_EMAIL)).thenReturn(customer);

        UserDetails user = userDetailsService.loadUserByUsername(CUSTOMER_EMAIL);

        assertThat(user, Matchers.instanceOf(AuthenticatedCustomer.class));
        verify(customerService, times(1)).getCustomerByEmail(CUSTOMER_EMAIL);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_UnknownEmailGiven_ExceptionThrown() {
        when(customerService.getCustomerByEmail(CUSTOMER_EMAIL)).thenReturn(null);

        userDetailsService.loadUserByUsername(CUSTOMER_EMAIL);
    }

    private Customer customerWithEmailAndPassword(String email, String password) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        return customer;
    }
}