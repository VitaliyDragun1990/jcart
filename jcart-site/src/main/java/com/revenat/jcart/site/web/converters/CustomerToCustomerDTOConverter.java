package com.revenat.jcart.site.web.converters;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.site.web.dto.CustomerDTO;
import org.springframework.core.convert.converter.Converter;

public class CustomerToCustomerDTOConverter implements Converter<Customer, CustomerDTO> {

    @Override
    public CustomerDTO convert(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPassword(customer.getPassword());
        dto.setPhone(customer.getPhone());

        return dto;
    }
}
