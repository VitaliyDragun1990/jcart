package com.revenat.jcart.site.web.converters;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.site.web.dto.CustomerDTO;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static com.revenat.jcart.site.web.converters.CustomerDTOToCustomerConverterTest.SamePropertyValues.hasSamePropertyValuesAs;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class CustomerDTOToCustomerConverterTest {

    private static final Integer ID = 1;
    private static final String FIRST_NAME = "Dummy";
    private static final String LAST_NAME = "Test";
    private static final String EMAIL = "dummy@test.com";
    private static final String PASSWORD = "test";
    private static final String PHONE_NUMBER = "999-444-5555";

    private CustomerDTOToCustomerConverter converter = new CustomerDTOToCustomerConverter();

    @Test
    public void convert_CustomerDtoGiven_CustomerEntityReceived() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(ID);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setPhone(PHONE_NUMBER);

        Customer customer = converter.convert(dto);

        assertThat(customer, hasSamePropertyValuesAs(dto));
    }

    static class SamePropertyValues extends TypeSafeMatcher<Customer> {

        private CustomerDTO dto;

        private SamePropertyValues(CustomerDTO dto) {
            this.dto = dto;
        }

        @Override
        protected boolean matchesSafely(Customer customer) {
            try {
                assertThat(customer.getId(), equalTo(dto.getId()));
                assertThat(customer.getFirstName(), equalTo(dto.getFirstName()));
                assertThat(customer.getLastName(), equalTo(dto.getLastName()));
                assertThat(customer.getPassword(), equalTo(dto.getPassword()));
                assertThat(customer.getEmail(), equalTo(dto.getEmail()));
                assertThat(customer.getPhone(), equalTo(dto.getPhone()));
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("has the same property values as " + dto.getClass().getSimpleName());
        }

        @Factory
        public static Matcher<Customer> hasSamePropertyValuesAs(CustomerDTO dto) {
            return new SamePropertyValues(dto);
        }
    }
}