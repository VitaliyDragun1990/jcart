package com.revenat.jcart.admin.web.controllers;

import com.revenat.config.MockSecurityServiceConfig;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {JCartAdminApplication.class, TestConfig.class,
        MockSecurityServiceConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class CustomerControllerTest {

    private static final String AUTH_USER_EMAIL = "john@gmail.com";
    private static final String HEADER_TITLE = "Manage Customers";

    private static final Integer CUSTOMER_ID = 1;
    private static final String CUSTOMER_FIRST_NAME = "Jack";
    private static final String CUSTOMER_EMAIL = "jack@gmail.com";
    private static final String VIEW_CUSTOMERS = "customers/customers";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerController customerController;

    private MockMvc mvc;

    @Before
    public void setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpMocks() {
        Mockito.reset(customerService);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void getHeader_ShouldReturnHeaderTitle() {
        assertThat(customerController.getHeaderTitle(), equalTo(HEADER_TITLE));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void listCustomers_ShouldAddCustomerEntriesToModel() throws Exception {
        Customer customer = createCustomerWith(CUSTOMER_FIRST_NAME, CUSTOMER_EMAIL, CUSTOMER_ID);
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(customer));

        MvcResult result = mvc.perform(get("/customers")).andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();
        verify(customerService, times(1)).getAllCustomers();
        assertThat(model, hasKey("customers"));
        List<Customer> customers = (List<Customer>) model.get("customers");
        assertThat(customers, hasItem(allOf(
                hasProperty("firstName", is(CUSTOMER_FIRST_NAME)),
                hasProperty("id", is(CUSTOMER_ID)),
                hasProperty("email", is(CUSTOMER_EMAIL))
        )));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void listCustomers_ShouldRenderCustomersView() throws Exception {
        Customer customer = createCustomerWith(CUSTOMER_FIRST_NAME, CUSTOMER_EMAIL, CUSTOMER_ID);
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(customer));

        MvcResult result = mvc.perform(get("/customers")).andReturn();

        String viewName = result.getModelAndView().getViewName();
        int status = result.getResponse().getStatus();
        assertThat(viewName, equalTo(VIEW_CUSTOMERS));
        assertThat(status, equalTo(HttpStatus.OK.value()));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void viewCustomerForm_ShouldAddCustomerEntryToModel() throws Exception {
        Customer customer = createCustomerWith(CUSTOMER_FIRST_NAME, CUSTOMER_EMAIL, CUSTOMER_ID);
        when(customerService.getCustomerById(CUSTOMER_ID)).thenReturn(customer);

        mvc.perform(get("/customers/"+CUSTOMER_ID))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attribute("customer",
                        allOf(
                                hasProperty("id", is(CUSTOMER_ID)),
                                hasProperty("firstName", is(CUSTOMER_FIRST_NAME)),
                                hasProperty("email", is(CUSTOMER_EMAIL))
                        )));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void viewCustomerForm_ShouldRenderViewCustomerView() throws Exception {
        Customer customer = createCustomerWith(CUSTOMER_FIRST_NAME, CUSTOMER_EMAIL, CUSTOMER_ID);
        when(customerService.getCustomerById(CUSTOMER_ID)).thenReturn(customer);

        mvc.perform(get("/customers/"+CUSTOMER_ID))
                .andExpect(view().name("customers/view_customer"))
                .andExpect(status().isOk());
    }

    private Customer createCustomerWith(String firstName, String email, Integer id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(firstName);
        customer.setEmail(email);
        return customer;
    }
}