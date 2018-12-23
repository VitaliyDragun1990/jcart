package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.admin.security.SecurityUtil;
import com.revenat.jcart.core.customers.CustomerService;
import com.revenat.jcart.core.entities.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@Secured(SecurityUtil.MANAGE_CUSTOMERS)
public class CustomerController extends JCartAdminBaseController {

    private static final String VIEW_PREFIX = "customers/";
    private static final String HEADER_TITLE = "Manage Customers";

    @Autowired
    private CustomerService customerService;

    @Override
    protected String getHeaderTitle() {
        return HEADER_TITLE;
    }

    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public String listCustomers(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return VIEW_PREFIX + "customers";
    }

    @RequestMapping(value = "/customers/{id}", method = RequestMethod.GET)
    public String viewCustomerForm(@PathVariable("id") Integer id, Model model) {
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        return VIEW_PREFIX + "view_customer";
    }
}
