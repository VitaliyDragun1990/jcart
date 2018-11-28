package com.revenat.jcart.admin.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController extends JCartAdminBaseController {

    @Override
    protected String getHeaderTitle() {
        return "Home";
    }

    @RequestMapping("/home")
    public String home() {
        return "home";
    }
}
