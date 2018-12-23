package com.revenat.jcart.core.orders;

import com.revenat.jcart.core.entities.Order;
import com.revenat.jcart.core.entities.OrderStatus;
import com.revenat.jcart.core.exceptions.JCartException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JCartOrderServiceTest {

    private static final String ORDER_NUMBER = "Order A";
    private static final int ORDER_ID = 1;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderNumberGenerator numberGenerator;

    @InjectMocks
    private JCartOrderService orderService;

    @Test
    public void createOrder_ShouldGenerateOrderNumberForOrder() {
        Order order = new Order();
        when(numberGenerator.generate()).thenReturn(ORDER_NUMBER);
        when(orderRepository.save(isA(Order.class))).thenAnswer(
                (Answer<Order>) invocationOnMock -> (Order) invocationOnMock.getArguments()[0]);

        orderService.createOrder(order);

        assertThat(order.getOrderNumber(), equalTo(ORDER_NUMBER));
    }

    @Test
    public void createOrder_ShouldSaveNewOrder() {
        Order order = new Order();
        when(numberGenerator.generate()).thenReturn(ORDER_NUMBER);
        when(orderRepository.save(isA(Order.class))).thenAnswer(
                (Answer<Order>) invocationOnMock -> (Order) invocationOnMock.getArguments()[0]);

        orderService.createOrder(order);

        verify(orderRepository, times(1)).save(isA(Order.class));
    }

    @Test
    public void createOrder_ShouldReturnCreatedOrder() {
        Order order = new Order();
        when(numberGenerator.generate()).thenReturn(ORDER_NUMBER);
        when(orderRepository.save(isA(Order.class))).thenAnswer(
                (Answer<Order>) invocationOnMock -> (Order) invocationOnMock.getArguments()[0]);

        Order createdOrder = orderService.createOrder(order);

        assertThat(createdOrder.getOrderNumber(), equalTo(ORDER_NUMBER));
    }

    @Test
    public void getOrder_OrderNumberGiven_ShouldReturnMatchingOrder() {
        when(orderRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(new Order());

        Order order = orderService.getOrder(ORDER_NUMBER);

        assertNotNull(order);
        verify(orderRepository, times(1)).findByOrderNumber(ORDER_NUMBER);
    }

    @Test
    public void getAllOrders_ShouldReturnAllOrders() {
        when(orderRepository.findAll(isA(Sort.class))).thenReturn(Arrays.asList(new Order()));

        List<Order> allOrders = orderService.getAllOrders();

        verify(orderRepository, times(1)).findAll(isA(Sort.class));
        assertThat(allOrders, hasSize(1));
    }

    @Test
    public void updateOrder_OrderFound_StatusUpdated() {
        Order updateHolder = new Order();
        updateHolder.setOrderNumber(ORDER_NUMBER);
        updateHolder.setStatus(OrderStatus.COMPLETED);
        Order orderToUpdate = new Order();
        orderToUpdate.setStatus(OrderStatus.NEW);
        when(orderRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(orderToUpdate);

        orderService.updateOrder(updateHolder);

        verify(orderRepository, times(1)).findByOrderNumber(ORDER_NUMBER);
        assertThat(orderToUpdate.getStatus(), equalTo(OrderStatus.COMPLETED));
    }

    @Test
    public void updateOrder_OrderFound_UpdatedOrderSaved() {
        Order updateHolder = new Order();
        updateHolder.setOrderNumber(ORDER_NUMBER);
        updateHolder.setStatus(OrderStatus.COMPLETED);
        Order orderToUpdate = new Order();
        when(orderRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(orderToUpdate);

        orderService.updateOrder(updateHolder);

        verify(orderRepository, times(1)).save(orderToUpdate);
    }

    @Test
    public void updatedOrder_OrderFound_UpdatedOrderReturned() {
        Order updateHolder = new Order();
        updateHolder.setOrderNumber(ORDER_NUMBER);
        updateHolder.setStatus(OrderStatus.COMPLETED);
        Order orderToUpdate = new Order();
        orderToUpdate.setId(ORDER_ID);
        when(orderRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(orderToUpdate);
        when(orderRepository.save(isA(Order.class))).thenAnswer(
                (Answer<Order>) invocationOnMock -> (Order) invocationOnMock.getArguments()[0]);

        Order updatedOrder = orderService.updateOrder(updateHolder);

        assertThat(updatedOrder.getStatus(), equalTo(OrderStatus.COMPLETED));
    }

    @Test(expected = JCartException.class)
    public void updateOrder_OrderNotFound_ExceptionThrown() {
        Order updateHolder = new Order();
        updateHolder.setOrderNumber(ORDER_NUMBER);
       when(orderRepository.findByOrderNumber(anyString())).thenReturn(null);

        orderService.updateOrder(updateHolder);
    }
}