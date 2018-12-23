package com.revenat.jcart.site.web.validators;

import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Product;
import com.revenat.jcart.site.web.dto.CustomerDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerValidatorTest {

    private static final String CUSTOMER_EMAIL = "dummy@gmail.com";

    @Mock
    private CustomerService customerService;
    @Mock
    private Errors errors;

    @InjectMocks
    private CustomerValidator validator;

    @Test
    public void validate_UniqueEmail_ShouldNotGetANyErrors() {
        CustomerDTO customer = new CustomerDTO();
        customer.setEmail(CUSTOMER_EMAIL);
        when(customerService.getCustomerByEmail(CUSTOMER_EMAIL)).thenReturn(null);

        validator.validate(customer, errors);

        verify(customerService, times(1)).getCustomerByEmail(CUSTOMER_EMAIL);
        verifyZeroInteractions(errors);
    }

    @Test
    public void validate_EmailAlreadyInUSe_ShouldAddValidationError() {
        CustomerDTO customer = new CustomerDTO();
        customer.setEmail(CUSTOMER_EMAIL);
        when(customerService.getCustomerByEmail(CUSTOMER_EMAIL)).thenReturn(new Customer());

        validator.validate(customer, errors);

        verify(customerService, times(1)).getCustomerByEmail(CUSTOMER_EMAIL);
        verify(errors, times(1)).rejectValue(anyString(), anyString(), any(), anyString());
    }

    @Test
    public void supports_CustomerClass_ReturnsTrue() {
        assertTrue("Should support validation for CustomerDTO type.", validator.supports(CustomerDTO.class));
    }

    @Test
    public void supports_WrongType_ReturnsFalse() {
        assertFalse("Should not support validation for any class except CustomerDTO one.", validator.supports(Product.class));
    }
}