package com.revenat.jcart.site.web.controllers;

import com.revenat.builders.OrderDTOBuilder;
import com.revenat.builders.ProductBuilder;
import com.revenat.config.SecurityConfigurer;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartSiteApplication;
import com.revenat.jcart.core.common.services.EmailService;
import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.*;
import com.revenat.jcart.core.orders.OrderService;
import com.revenat.jcart.site.web.dto.OrderDTO;
import com.revenat.jcart.site.web.models.Cart;
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartSiteApplication.class, TestConfig.class}
)
@WebAppConfiguration
@ActiveProfiles("test")
public class OrderControllerTest {

    private static final String CUSTOMER_EMAIL = "john@gmail.com";

    private static final String HEADER_TITLE = "Order";
    private static final String CREDIT_CARD_NUMBER = "Credit Card Number";
    private static final String CVV = "CVV";
    private static final String PRODUCT_NAME = "Product name";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.ONE;
    private static final String ADDRESS_LINE = "Address Line";
    private static final String CITY = "City";
    private static final String STATE = "State";
    private static final String ZIP_CODE = "Zip Code";
    private static final String COUNTRY = "Country";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Smith";
    private static final String PHONE = "555-555-58455";
    private static final String CART_ATTRIBUTE = "CART_KEY";
    private static final String ORDER_NUMBER = "orderA";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OrderController controller;

    private MockMvc mvc;

    @Before
    public void setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpMocks() {
        Mockito.reset(orderService, customerService, emailService);
        SecurityConfigurer.configureAuthenticatedCustomer(customerService, CUSTOMER_EMAIL);
        returnCreatedOrder();
    }

    private void returnCreatedOrder() {
        when(orderService.createOrder(isA(Order.class))).thenAnswer(
                (Answer<Order>) invocationOnMock -> (Order) invocationOnMock.getArguments()[0]);
    }

    @Test
    public void getHeaderTitle_ShouldReturnHeaderTitle() {
        assertThat(controller.getHeaderTitle(), is(HEADER_TITLE));
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    public void placeOrder_NewOrderEntry_ShouldCreateNewOrder() throws Exception {
        OrderDTO orderDTO = createValidOrderDTO();
        Cart cart = createCartWithProduct(PRODUCT_NAME, PRODUCT_PRICE);

        mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders", orderDTO)
                .sessionAttr(CART_ATTRIBUTE, cart))
                .andDo(print());

        verify(orderService, times(1)).createOrder(isA(Order.class));
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    public void placeOrder_NewOrderEntry_CreatedOrderShouldContainCorrectData() throws Exception {
        OrderDTO orderDTO = createValidOrderDTO();
        Cart cart = createCartWithProduct(PRODUCT_NAME, PRODUCT_PRICE);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders", orderDTO)
                .sessionAttr(CART_ATTRIBUTE, cart))
                .andDo(print());

        verify(orderService, times(1)).createOrder(orderCaptor.capture());
        Order createdOrder = orderCaptor.getValue();
        assertOrderProperties(createdOrder);
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    public void placeOrder_NewOrderEntry_ShouldSendConfirmationEmail() throws Exception {
        OrderDTO orderDTO = createValidOrderDTO();
        Cart cart = createCartWithProduct(PRODUCT_NAME, PRODUCT_PRICE);

        mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders", orderDTO)
                .sessionAttr(CART_ATTRIBUTE, cart))
                .andDo(print());

        verify(emailService, times(1)).sendEmail(eq(CUSTOMER_EMAIL), anyString(), anyString());
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    public void placeNewOrder_NewOrderEntry_ShouldRemoveCartFromSessionAfterOrderCreation() throws Exception {
        OrderDTO orderDTO = createValidOrderDTO();
        Cart cart = createCartWithProduct(PRODUCT_NAME, PRODUCT_PRICE);

        MockHttpServletRequest request = mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders", orderDTO)
                .sessionAttr(CART_ATTRIBUTE, cart))
                .andDo(print())
                .andReturn().getRequest();

        Cart cartFromSession = (Cart) request.getSession().getAttribute(CART_ATTRIBUTE);
        assertNull(cartFromSession);
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    public void placeNewOrder_NewOrderEntry_ShouldRenderOrderConfirmationView() throws Exception {
        OrderDTO orderDTO = createValidOrderDTO();
        Cart cart = createCartWithProduct(PRODUCT_NAME, PRODUCT_PRICE);

        mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders", orderDTO)
                .sessionAttr(CART_ATTRIBUTE, cart))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/orderconfirmation*"));
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    public void placeNewOrder_IncompleteNewOrderEntry_ShouldRenderCheckoutView() throws Exception {
        OrderDTO orderDTO = createOrderDTOWithoutCreditCardNumber();
        Cart cart = createCartWithProduct(PRODUCT_NAME, PRODUCT_PRICE);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders", orderDTO)
                .sessionAttr(CART_ATTRIBUTE, cart))
                .andDo(print())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(mvcResult.getModelAndView().getViewName(), is("auth/checkout"));
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    public void placeNewOrder_IncompleteNewOrderEntry_ShouldReturnValidationErrors() throws Exception {
        OrderDTO orderDTO = createOrderDTOWithoutCreditCardNumber();
        Cart cart = createCartWithProduct(PRODUCT_NAME, PRODUCT_PRICE);

        mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders", orderDTO)
                .sessionAttr(CART_ATTRIBUTE, cart))
                .andDo(print())
                .andExpect(model().attributeErrorCount("order", 1))
                .andExpect(model().attributeHasFieldErrors("order", "ccNumber"));
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    public void showOrderConfirmation_ShouldAddOrderEntryToModelAndRenderConfirmationView() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/orderconfirmation").param("orderNumber", ORDER_NUMBER))
                .andDo(print())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
        assertThat(mvcResult.getModelAndView().getViewName(), equalTo("auth/order_confirmation"));
        Map<String, Object> model = mvcResult.getModelAndView().getModel();
        assertThat(model, Matchers.hasKey("order"));
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    public void viewOrder_ShouldAddOrderEntryToModelAndRenderViewOrderView() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/orders/"+ORDER_NUMBER))
                .andDo(print())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus(), equalTo(HttpStatus.OK.value()));
        assertThat(mvcResult.getModelAndView().getViewName(), equalTo("auth/view_order"));
        Map<String, Object> model = mvcResult.getModelAndView().getModel();
        assertThat(model, Matchers.hasKey("order"));
    }

    private OrderDTO createValidOrderDTO() {
        return OrderDTOBuilder.getBuilder()
                .withFirstName(FIRST_NAME)
                .withLastName(LAST_NAME)
                .withPhone(PHONE)
                .withAddress(ADDRESS_LINE)
                .withEmail(CUSTOMER_EMAIL)
                .withCity(CITY)
                .withState(STATE)
                .withZipCode(ZIP_CODE)
                .withCountry(COUNTRY)
                .withCreditCardNumber(CREDIT_CARD_NUMBER)
                .withCVV(CVV)
                .build();
    }

    private OrderDTO createOrderDTOWithoutCreditCardNumber() {
        return OrderDTOBuilder.getBuilder()
                .withFirstName(FIRST_NAME)
                .withLastName(LAST_NAME)
                .withPhone(PHONE)
                .withAddress(ADDRESS_LINE)
                .withEmail(CUSTOMER_EMAIL)
                .withCity(CITY)
                .withState(STATE)
                .withZipCode(ZIP_CODE)
                .withCountry(COUNTRY)
                .withCVV(CVV)
                .build();
    }

    private void assertOrderProperties(Order createdOrder) {
        assertAddress(createdOrder.getDeliveryAddress());
        assertAddress(createdOrder.getBillingAddress());
        assertPayment(createdOrder.getPayment());
        assertOrderItems(createdOrder.getItems());
        assertOrderCustomer(createdOrder.getCustomer());
    }

    private Cart createCartWithProduct(String productName, BigDecimal productPrice) {
        Product product = new ProductBuilder().withName(productName).withPrice(productPrice).build();
        Cart cart = new Cart();
        cart.addItem(product);
        return cart;
    }

    private void assertAddress(Address address) {
        assertThat(address.getAddressLine1(), is(ADDRESS_LINE));
        assertThat(address.getCity(), is(CITY));
        assertThat(address.getState(), is(STATE));
        assertThat(address.getZipCode(), is(ZIP_CODE));
        assertThat(address.getCountry(), is(COUNTRY));
    }

    private void assertPayment(Payment payment) {
        assertThat(payment.getCcNumber(), is(CREDIT_CARD_NUMBER));
        assertThat(payment.getCvv(), is(CVV));
    }

    private void assertOrderItems(Set<OrderItem> orderItems) {
        assertThat(orderItems, hasSize(1));
        OrderItem orderItem = orderItems.iterator().next();
        assertThat(orderItem, hasProperty("product", allOf(
                hasProperty("name", is(PRODUCT_NAME)),
                hasProperty("price", is(PRODUCT_PRICE))
        )));
    }

    private void assertOrderCustomer(Customer customer) {
        assertThat(customer, hasProperty("email", is(CUSTOMER_EMAIL)));
    }
}