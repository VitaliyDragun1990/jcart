package com.revenat.jcart.site.security;

import com.revenat.jcart.core.entities.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AuthenticatedCustomer extends User {

    private static final long serialVersionUID = 1L;
    private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    private Customer customer;

    AuthenticatedCustomer(Customer customer) {
        super(customer.getEmail(), customer.getPassword(), getAuthoritiesForCustomer());
        this.customer = customer;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    private static Collection<? extends GrantedAuthority> getAuthoritiesForCustomer() {
        return AuthorityUtils.createAuthorityList(ROLE_CUSTOMER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AuthenticatedCustomer that = (AuthenticatedCustomer) o;

        return customer != null ? customer.equals(that.customer) : that.customer == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        return result;
    }
}
