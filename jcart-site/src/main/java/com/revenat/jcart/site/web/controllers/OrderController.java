package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.common.services.EmailService;
import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;
import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.core.orders.OrderService;
import com.revenat.jcart.site.web.dto.OrderDTO;
import com.revenat.jcart.site.web.models.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
class OrderController extends JCartSiteBaseController {

    private static final String HEADER_TITLE = "Order";
    private static final String EMAIL_SUBJECT = "JCart - Order Confirmation";
    private static final String EMAIL_CONTENT = "You order has been placed successfully.\nOrder Number : ";

    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private EmailService emailService;

    @Override
    String getHeaderTitle() {
        return HEADER_TITLE;
    }

    @Secured(ROLE_CUSTOMER)
    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public String placeOrder(@Valid @ModelAttribute("order") OrderDTO order, BindingResult result, Model model,
                             HttpServletRequest request) {
        Cart cart = getOrCreateCart(request);
        if (result.hasErrors()) {
            model.addAttribute("cart", cart);
            return "auth/checkout";
        }

        String customerEmail = getCurrentCustomer().getCustomer().getEmail();
        Customer customer = customerService.getCustomerByEmail(customerEmail);

        Order customerOrder = OrderHelper.buildOrder(order, cart, customer);

        Order savedOrder = orderService.createOrder(customerOrder);

        sendOrderConfirmationEmail(savedOrder);

        request.getSession().removeAttribute(CART_KEY);

        return "redirect:/orderconfirmation?orderNumber=" + savedOrder.getOrderNumber();
    }

    @Secured(ROLE_CUSTOMER)
    @RequestMapping(value = "/orderconfirmation", method = RequestMethod.GET, params = {"orderNumber"})
    public String showOrderConfirmation(@RequestParam("orderNumber") String orderNumber, Model model) {
        Order order = orderService.getOrder(orderNumber);
        model.addAttribute("order", order);
        return "auth/order_confirmation";
    }

    @Secured(ROLE_CUSTOMER)
    @RequestMapping(value = "/orders/{orderNumber}", method = RequestMethod.GET)
    public String viewOrder(@PathVariable("orderNumber") String orderNumber, Model model) {
        Order order = orderService.getOrder(orderNumber);
        model.addAttribute("order", order);
        return "auth/view_order";
    }

    private void sendOrderConfirmationEmail(Order order) {
        try {
            emailService.sendEmail(
                    order.getCustomer().getEmail(),
                    EMAIL_SUBJECT,
                    EMAIL_CONTENT + order.getOrderNumber());
        } catch (JCartException e) {
            logger.error(e);
        }
    }
}
