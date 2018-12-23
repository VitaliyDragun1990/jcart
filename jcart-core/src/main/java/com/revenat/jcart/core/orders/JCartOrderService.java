package com.revenat.jcart.core.orders;

import com.revenat.jcart.core.common.services.JCLogger;
import com.revenat.jcart.core.entities.Order;
import com.revenat.jcart.core.exceptions.JCartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
class JCartOrderService implements OrderService {

    private static final JCLogger LOGGER = JCLogger.getLogger(JCartOrderService.class);

    private OrderRepository orderRepository;
    private OrderNumberGenerator numberGenerator;

    @Autowired
    public JCartOrderService(OrderRepository orderRepository, OrderNumberGenerator numberGenerator) {
        this.orderRepository = orderRepository;
        this.numberGenerator = numberGenerator;
    }

    @Override
    public Order createOrder(Order order) {
        order.setOrderNumber(numberGenerator.generate());
        Order savedOrder = orderRepository.save(order);
        LOGGER.info("New order created. Order Number : {}", savedOrder.getOrderNumber());
        return savedOrder;
    }

    @Override
    public Order getOrder(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public List<Order> getAllOrders() {
        Sort sort = new Sort(Sort.Direction.DESC, "createdOn");
        return orderRepository.findAll(sort);
    }

    @Override
    public Order updateOrder(Order order) {
        Order orderToUpdate = getOrder(order.getOrderNumber());
        if (orderToUpdate == null) {
            throw new JCartException("Invalid Order Number : " + order.getOrderNumber());
        }
        orderToUpdate.setStatus(order.getStatus());
        return orderRepository.save(orderToUpdate);
    }
}
