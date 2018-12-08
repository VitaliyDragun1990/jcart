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

    @Mock
    private CustomerService mockCustomerService;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    public void loadsUserIfEmailExists() {
        String userEmail = "john@gmail.com";
        Customer customer = new Customer();
        customer.setEmail(userEmail);
        customer.setPassword("test");
        when(mockCustomerService.getCustomerByEmail(userEmail)).thenReturn(customer);

        UserDetails user = userDetailsService.loadUserByUsername(userEmail);

        assertThat(user, Matchers.instanceOf(AuthenticatedCustomer.class));
        verify(mockCustomerService, times(1)).getCustomerByEmail(userEmail);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void throwsExceptionIfNoUserWithGivenEmail() {
        String email = "dummy@test.com";

        userDetailsService.loadUserByUsername(email);
    }
}