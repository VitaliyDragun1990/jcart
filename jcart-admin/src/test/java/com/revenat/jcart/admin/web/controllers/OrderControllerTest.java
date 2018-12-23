package com.revenat.jcart.admin.web.controllers;

import com.revenat.config.MockSecurityServiceConfig;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.admin.web.controllers.builders.CustomerBuilder;
import com.revenat.jcart.admin.web.controllers.builders.OrderBuilder;
import com.revenat.jcart.admin.web.controllers.builders.OrderItemBuilder;
import com.revenat.jcart.admin.web.controllers.builders.ProductBuilder;
import com.revenat.jcart.admin.web.models.OrderForm;
import com.revenat.jcart.core.common.services.EmailService;
import com.revenat.jcart.core.entities.Order;
import com.revenat.jcart.core.entities.OrderStatus;
import com.revenat.jcart.core.orders.OrderService;
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
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

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {JCartAdminApplication.class, TestConfig.class,
        MockSecurityServiceConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class OrderControllerTest {

    private static final String AUTH_USER_EMAIL = "john@gmail.com";
    private static final String ORDER_NUMBER = "OrderNumber";
    private static final String CUSTOMER_EMAIL = "test@gmail.com";
    private static final String CUSTOMER_FIRST_NAME = "Jack";
    private static final String VIEW_ORDERS = "orders/orders";
    private static final BigDecimal ITEM_PRICE = BigDecimal.TEN;
    private static final int ITEM_QUANTITY = 1;
    private static final String PRODUCT_NAME = "ProductName";
    private static final String VIEW_EDIT_ORDER = "orders/edit_order";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EmailService emailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderController orderController;

    private MockMvc mvc;

    @Before
    public void setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpMocks() {
        Mockito.reset(emailService, orderService);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void getHeaderTitle_ShouldReturnHeaderTitle() {
        assertThat(orderController.getHeaderTitle(), equalTo("Manage Orders"));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void listOrders_ShouldAddAllOrderEntriesToModel() throws Exception {
        Order order = buildOrderWithOneItem();
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(order));

        mvc.perform(get("/orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", hasSize(1)))
                .andExpect(model().attribute("orders", hasItem(hasProperty("orderNumber", is(ORDER_NUMBER)))));

    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void listOrders_ShouldRenderOrdersView() throws Exception {
        Order order = buildOrderWithOneItem();
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(order));

        mvc.perform(get("/orders"))
                .andExpect(view().name(VIEW_ORDERS))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editOrderForm_ShouldAddOrderEntryToModel() throws Exception {
        Order order = buildOrderWithOneItem();
        when(orderService.getOrder(ORDER_NUMBER)).thenReturn(order);

        mvc.perform(get("/orders/"+ORDER_NUMBER))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("order", allOf(
                        hasProperty("customer", hasProperty("email", is(CUSTOMER_EMAIL))),
                        hasProperty("orderNumber", is(ORDER_NUMBER))
                )));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editOrderForm_ShouldRenderEditOrderView() throws Exception {
        Order order = buildOrderWithOneItem();
        when(orderService.getOrder(ORDER_NUMBER)).thenReturn(order);

        mvc.perform(get("/orders/"+ORDER_NUMBER))
                .andExpect(view().name(VIEW_EDIT_ORDER));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateOrder_ShouldUpdateGivenOrder() throws Exception {
        OrderForm form = OrderForm.fromOrder(buildOrderWithOneItem());
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderService.updateOrder(Matchers.isA(Order.class))).thenAnswer(
                (Answer<Order>) invocationOnMock -> (Order) invocationOnMock.getArguments()[0]);

        mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders/"+ORDER_NUMBER, form));

        verify(orderService, times(1)).updateOrder(orderCaptor.capture());
        Order orderToUpdate = orderCaptor.getValue();
        assertThat(orderToUpdate.getOrderNumber(), equalTo(ORDER_NUMBER));
        assertThat(orderToUpdate.getStatus(), equalTo(OrderStatus.NEW));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateOrder_ShouldSendUpdateEmail() throws Exception {
        OrderForm form = OrderForm.fromOrder(buildOrderWithOneItem());
        when(orderService.updateOrder(Matchers.isA(Order.class))).thenAnswer(
                (Answer<Order>) invocationOnMock -> (Order) invocationOnMock.getArguments()[0]);

        mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders/"+ORDER_NUMBER, form));

        verify(emailService, times(1)).sendEmail(eq(CUSTOMER_EMAIL), anyString(), anyString());
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateOrder_ShouldRenderOrdersView() throws Exception {
        OrderForm form = OrderForm.fromOrder(buildOrderWithOneItem());
        when(orderService.updateOrder(Matchers.isA(Order.class))).thenAnswer(
                (Answer<Order>) invocationOnMock -> (Order) invocationOnMock.getArguments()[0]);

        mvc.perform(MockMvcRequestBuilderUtils.postForm("/orders/"+ORDER_NUMBER, form))
                .andExpect(redirectedUrl("/orders"))
                .andExpect(flash().attributeExists("info"));
    }

    private Order buildOrderWithOneItem() {
        return OrderBuilder.getBuilder()
                .withOrderNumber(ORDER_NUMBER)
                .withCustomer(CustomerBuilder.getBuilder()
                        .withEmail(CUSTOMER_EMAIL)
                        .withFirstName(CUSTOMER_FIRST_NAME).build())
                .withOrderItem(OrderItemBuilder.getBuilder().withPrice(ITEM_PRICE)
                        .withQuantity(ITEM_QUANTITY).withProduct(ProductBuilder.getBuilder()
                                .withName(PRODUCT_NAME).build()).build())
                .withStatus(OrderStatus.NEW)
                .build();
    }
}