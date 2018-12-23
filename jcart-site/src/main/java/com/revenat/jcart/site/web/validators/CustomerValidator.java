package com.revenat.jcart.site.web.validators;

import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.site.web.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("customerValidator")
public class CustomerValidator implements Validator {

    @Autowired
    private CustomerService customerService;

    @Override
    public boolean supports(Class<?> aClass) {
        return CustomerDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CustomerDTO customer = (CustomerDTO) target;
        Customer customerByEmail = customerService.getCustomerByEmail(customer.getEmail());
        if (customerByEmail != null) {
            errors.rejectValue("email", "error.exists", new Object[]{customer.getEmail()},
                    "Email " + customer.getEmail() + " already in use.");
        }
    }
}
