package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Order;
import com.revenat.jcart.site.web.dto.CustomerDTO;
import com.revenat.jcart.site.web.utils.MessageCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.util.List;


@Controller
class CustomerController extends JCartSiteBaseController {

    private static final String HEADER_TITLE = "Login/Register";

    @Autowired
    private CustomerService customerService;
    @Autowired
    @Qualifier("customerValidator")
    private Validator customerValidator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ConversionService conversionService;

    @Override
    String getHeaderTitle() {
        return HEADER_TITLE;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        return "public/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@Valid @ModelAttribute("customer") CustomerDTO customer, BindingResult result,
                    RedirectAttributes redirectAttributes) {
        customerValidator.validate(customer, result);
        if (result.hasErrors()) {
            return "public/register";
        }

        String password = customer.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        customer.setPassword(encodedPassword);

        Customer persistedCustomer = customerService.createCustomer(conversionService.convert(customer, Customer.class));
        logger.debug("Created new Customer with id : {} and email : {}", persistedCustomer.getId(), persistedCustomer.getEmail());

        redirectAttributes.addFlashAttribute("info", getMessage(MessageCodes.CUSTOMER_CREATED_SUCCESSFULLY));
        return "redirect:/login";
    }

    @Secured(ROLE_CUSTOMER)
    @RequestMapping(value = "/myAccount", method = RequestMethod.GET)
    public String myAccount(Model model) {
        String email = getCurrentCustomer().getCustomer().getEmail();
        Customer customer = customerService.getCustomerByEmail(email);
        model.addAttribute("customer", customer);
        List<Order> orders = customerService.getCustomerOrders(email);
        model.addAttribute("orders", orders);

        return "auth/myAccount";
    }
}
