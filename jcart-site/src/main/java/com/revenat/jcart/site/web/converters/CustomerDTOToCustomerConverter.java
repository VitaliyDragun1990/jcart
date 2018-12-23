package com.revenat.jcart.site.web.converters;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.site.web.dto.CustomerDTO;
import org.springframework.core.convert.converter.Converter;

public class CustomerDTOToCustomerConverter implements Converter<CustomerDTO, Customer> {

    @Override
    public Customer convert(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPassword(dto.getPassword());
        customer.setPhone(dto.getPhone());

        return customer;
    }
}
