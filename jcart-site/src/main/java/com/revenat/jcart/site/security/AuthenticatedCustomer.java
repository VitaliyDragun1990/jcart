package com.revenat.jcart.site.security;

import com.revenat.jcart.core.entities.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AuthenticatedCustomer extends User {

    private static final long serialVersionUID = 1L;
    private Customer customer;

    public AuthenticatedCustomer(Customer customer) {
        super(customer.getEmail(), customer.getPassword(), getAuthoritiesForCustomer());
    }

    public Customer getCustomer() {
        return customer;
    }

    private static Collection<? extends GrantedAuthority> getAuthoritiesForCustomer() {
        return AuthorityUtils.createAuthorityList("ROLE_USER");
    }
}
