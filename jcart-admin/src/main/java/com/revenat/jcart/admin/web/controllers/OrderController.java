package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.admin.security.SecurityUtil;
import com.revenat.jcart.admin.web.models.OrderForm;
import com.revenat.jcart.core.common.services.EmailService;
import com.revenat.jcart.core.entities.Order;
import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.core.orders.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

import static com.revenat.jcart.admin.web.utils.MessageCodes.INFO_ORDER_STATUS_UPDATE;
import static com.revenat.jcart.admin.web.utils.MessageCodes.INFO_ORDER_UPDATED_SUCCESSFULLY;
import static java.util.stream.Collectors.toList;

@Controller
@Secured(SecurityUtil.MANAGE_ORDERS)
public class OrderController extends JCartAdminBaseController {

    private static final String VIEW_PREFIX = "orders/";
    private static final String EMAIL_TEMPLATE_NAME = "order-status-update-email";
    private static final String HEADER_TITLE = "Manage Orders";

    @Autowired
    private EmailService emailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    protected String getHeaderTitle() {
        return HEADER_TITLE;
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        List<OrderForm> forms = orders.stream().map(OrderForm::fromOrder).collect(toList());
        model.addAttribute("orders", forms);
        return VIEW_PREFIX + "orders";
    }

    @RequestMapping(value = "/orders/{orderNumber}", method = RequestMethod.GET)
    public String editOrderForm(@PathVariable("orderNumber") String orderNumber, Model model) {
        Order order = orderService.getOrder(orderNumber);
        OrderForm form = OrderForm.fromOrder(order);
        model.addAttribute("order", form);
        return VIEW_PREFIX + "edit_order";
    }

    @RequestMapping(value = "/orders/{orderNumber}", method = RequestMethod.POST)
    public String updateOrder(@ModelAttribute("order") OrderForm order, RedirectAttributes redirectAttributes) {
        Order persistedOrder = orderService.updateOrder(order.toOrder());
        sendOrderStatusUpdateEmail(persistedOrder);
        LOGGER.debug("Update order with orderNumber : {}", persistedOrder.getOrderNumber());
        redirectAttributes.addFlashAttribute("info", getMessage(INFO_ORDER_UPDATED_SUCCESSFULLY));
        return "redirect:/orders";
    }

    private void sendOrderStatusUpdateEmail(Order order) {
        try {
            // Prepare the evaluation context
            final Context context = new Context();
            context.setVariable("order", order);

            // Create the HTML body using Thymeleaf
            final String emailContent = templateEngine.process(EMAIL_TEMPLATE_NAME, context);

            emailService.sendEmail(
                    order.getCustomer().getEmail(),
                    getMessage(INFO_ORDER_STATUS_UPDATE),
                    emailContent
            );
        } catch (JCartException e) {
            LOGGER.error(e);
        }
    }
}
