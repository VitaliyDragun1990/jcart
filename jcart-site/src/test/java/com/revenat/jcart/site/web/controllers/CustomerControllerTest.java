package com.revenat.jcart.site.web.controllers;

import com.revenat.builders.CustomerBuilder;
import com.revenat.config.SecurityConfigurer;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartSiteApplication;
import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;
import com.revenat.jcart.core.entities.OrderStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartSiteApplication.class, TestConfig.class}
)
@WebAppConfiguration
@ActiveProfiles("test")
public class CustomerControllerTest {

    private static final String AUTH_CUSTOMER_EMAIL = "john@gmail.com";

    private static final String HEADER_TITLE = "Login/Register";
    private static final String VIEW_REGISTER = "public/register";
    private static final String CUSTOMER_FIRST_NAME = "Jack";
    private static final String CUSTOMER_LAST_NAME = "Smith";
    private static final String CUSTOMER_EMAIL = "anna@gmail.com";
    private static final String CUSTOMER_PASSWORD = "password";
    private static final String CUSTOMER_PHONE = "555-555-7777";
    private static final String ORDER_NUMBER = "order number";
    private static final OrderStatus ORDER_STATUS = OrderStatus.NEW;
    private static final String VIEW_MY_ACCOUNT = "auth/myAccount";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CustomerController controller;
    @Autowired
    private CustomerService customerService;

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
        SecurityConfigurer.configureAuthenticatedCustomer(customerService, AUTH_CUSTOMER_EMAIL);
    }

    @Test
    public void getHeaderTitle_ShouldReturnHeaderTitle() {
        assertThat(controller.getHeaderTitle(), equalTo(HEADER_TITLE));
    }

    @Test
    public void registerForm_ShouldAddCustomerEntryToModelAndRenderRegistryView() throws Exception {
        mvc.perform(get("/register"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customer"))
                .andExpect(view().name(VIEW_REGISTER));
    }

    @Test
    public void register_NewCustomerEntry_ShouldCreateNewCustomerAndRenderLoginView() throws Exception {
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        when(customerService.createCustomer(isA(Customer.class))).thenAnswer(
                (Answer<Customer>) invocationOnMock -> (Customer) invocationOnMock.getArguments()[0]);

        mvc.perform(post("/register")
                .param("firstName", CUSTOMER_FIRST_NAME)
                .param("lastName", CUSTOMER_LAST_NAME)
                .param("email", CUSTOMER_EMAIL)
                .param("password", CUSTOMER_PASSWORD)
                .param("phone", CUSTOMER_PHONE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("info"));

        verify(customerService, times(1)).createCustomer(customerCaptor.capture());
        Customer createdCustomer = customerCaptor.getValue();
        assertThat(createdCustomer,
                allOf(
                        hasProperty("firstName", is(CUSTOMER_FIRST_NAME)),
                        hasProperty("lastName", is(CUSTOMER_LAST_NAME)),
                        hasProperty("email", is(CUSTOMER_EMAIL)),
                        hasProperty("phone", is(CUSTOMER_PHONE)),
                        hasProperty("password", is(notNullValue()))
                ));
    }

    @Test
    public void register_EmailAlreadyTaken_ShouldRenderRegisterViewAndReturnValidationErrors() throws Exception {
        Customer customer = createCustomerWithEmail(CUSTOMER_EMAIL);
        when(customerService.getCustomerByEmail(CUSTOMER_EMAIL)).thenReturn(customer);

        mvc.perform(post("/register")
                .param("firstName", CUSTOMER_FIRST_NAME)
                .param("lastName", CUSTOMER_LAST_NAME)
                .param("email", CUSTOMER_EMAIL)
                .param("password", CUSTOMER_PASSWORD)
                .param("phone", CUSTOMER_PHONE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customer"))
                .andExpect(view().name(VIEW_REGISTER));

        verify(customerService, atLeast(1)).getCustomerByEmail(CUSTOMER_EMAIL);
        verify(customerService, times(0)).createCustomer(customer);
    }

    @Test
    @WithUserDetails(AUTH_CUSTOMER_EMAIL)
    public void myAccount_ShouldAddCustomerEntryToModel() throws Exception {
        Customer customer = createCustomerWithEmail(AUTH_CUSTOMER_EMAIL);
        when(customerService.getCustomerByEmail(AUTH_CUSTOMER_EMAIL)).thenReturn(customer);

        mvc.perform(get("/myAccount"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attribute("customer",
                        allOf(
                                hasProperty("firstName", is(CUSTOMER_FIRST_NAME)),
                                hasProperty("lastName", is(CUSTOMER_LAST_NAME)),
                                hasProperty("email", is(AUTH_CUSTOMER_EMAIL)),
                                hasProperty("phone", is(CUSTOMER_PHONE))
                        )));

        verify(customerService).getCustomerByEmail(AUTH_CUSTOMER_EMAIL);
    }

    @Test
    @WithUserDetails(AUTH_CUSTOMER_EMAIL)
    public void myAccount_ShouldAddOrderEntriesToModel() throws Exception {
        Customer customer = createCustomerWithEmail(AUTH_CUSTOMER_EMAIL);
        Order order = createOrder(ORDER_NUMBER, ORDER_STATUS);
        when(customerService.getCustomerByEmail(AUTH_CUSTOMER_EMAIL)).thenReturn(customer);
        when(customerService.getCustomerOrders(AUTH_CUSTOMER_EMAIL)).thenReturn(Arrays.asList(order));

        mvc.perform(get("/myAccount"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", hasSize(1)))
                .andExpect(model().attribute("orders", hasItem(
                        allOf(
                                hasProperty("orderNumber", is(ORDER_NUMBER)),
                                hasProperty("status", is(ORDER_STATUS))
                        )
                )));

        verify(customerService).getCustomerOrders(AUTH_CUSTOMER_EMAIL);
    }

    @Test
    @WithUserDetails(AUTH_CUSTOMER_EMAIL)
    public void myAccount_ShouldRenderMyAccountView() throws Exception {
        Customer customer = createCustomerWithEmail(AUTH_CUSTOMER_EMAIL);
        Order order = createOrder(ORDER_NUMBER, ORDER_STATUS);
        when(customerService.getCustomerByEmail(AUTH_CUSTOMER_EMAIL)).thenReturn(customer);
        when(customerService.getCustomerOrders(AUTH_CUSTOMER_EMAIL)).thenReturn(Arrays.asList(order));

        mvc.perform(get("/myAccount"))
               .andExpect(view().name(VIEW_MY_ACCOUNT));

    }

    private Customer createCustomerWithEmail(String customerEmail) {
        return CustomerBuilder.getBuilder().withFirstName(CUSTOMER_FIRST_NAME)
                .withLastName(CUSTOMER_LAST_NAME).withEmail(customerEmail).withPhone(CUSTOMER_PHONE)
                .withPassword(CUSTOMER_PASSWORD).build();
    }

    private Order createOrder(String orderNumber, OrderStatus orderStatus) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(orderStatus);
        return order;
    }
}