package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.site.web.dto.OrderDTO;
import com.revenat.jcart.site.web.models.Cart;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.revenat.jcart.site.web.controllers.JCartSiteBaseController.ROLE_CUSTOMER;

@Controller
@Secured(ROLE_CUSTOMER)
class CheckoutController extends JCartSiteBaseController {

    private static final String HEADER_TITLE = "Checkout";

    @Override
    String getHeaderTitle() {
        return HEADER_TITLE;
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String checkout(HttpServletRequest request, Model model) {
        OrderDTO order = new OrderDTO();
        model.addAttribute("order", order);
        Cart cart = getOrCreateCart(request);
        model.addAttribute("cart", cart);

        return "auth/checkout";
    }
}
