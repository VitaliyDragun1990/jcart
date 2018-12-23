package com.revenat.jcart.site.web.converters;

import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.site.web.dto.CustomerDTO;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static com.revenat.jcart.site.web.converters.CustomerToCustomerDTOConverterTest.SamePropertyValues.hasSamePropertyValuesAs;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class CustomerToCustomerDTOConverterTest {

    private static final Integer ID = 1;
    private static final String FIRST_NAME = "Dummy";
    private static final String LAST_NAME = "Test";
    private static final String EMAIL = "dummy@test.com";
    private static final String PASSWORD = "test";
    private static final String PHONE_NUMBER = "999-444-5555";

    private CustomerToCustomerDTOConverter converter = new CustomerToCustomerDTOConverter();

    @Test
    public void convert_CustomerEntityGiven_CustomerDtoReceived() {
        Customer customer = new Customer();
        customer.setId(ID);
        customer.setFirstName(FIRST_NAME);
        customer.setLastName(LAST_NAME);
        customer.setEmail(EMAIL);
        customer.setPassword(PASSWORD);
        customer.setPhone(PHONE_NUMBER);

        CustomerDTO customerDTO = converter.convert(customer);

        assertThat(customerDTO, hasSamePropertyValuesAs(customer));
    }

    static class SamePropertyValues extends TypeSafeMatcher<CustomerDTO> {

        private Customer customer;

        private SamePropertyValues(Customer customer) {
            this.customer = customer;
        }

        @Override
        protected boolean matchesSafely(CustomerDTO dto) {
            try {
                assertThat(dto.getId(), equalTo(customer.getId()));
                assertThat(dto.getFirstName(), equalTo(customer.getFirstName()));
                assertThat(dto.getLastName(), equalTo(customer.getLastName()));
                assertThat(dto.getPassword(), equalTo(customer.getPassword()));
                assertThat(dto.getEmail(), equalTo(customer.getEmail()));
                assertThat(dto.getPhone(), equalTo(customer.getPhone()));
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("has the same property values as " + customer.getClass().getSimpleName());
        }

        public static Matcher<CustomerDTO> hasSamePropertyValuesAs(Customer customer) {
            return new SamePropertyValues(customer);
        }
    }
}