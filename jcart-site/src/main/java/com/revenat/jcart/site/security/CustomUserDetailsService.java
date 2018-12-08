package com.revenat.jcart.site.security;

import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerService customerService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Customer customer = customerService.getCustomerByEmail(email);
        if (customer == null) {
            throw new UsernameNotFoundException("Email " + email + " not found");
        }
        return new AuthenticatedCustomer(customer);
    }
}
