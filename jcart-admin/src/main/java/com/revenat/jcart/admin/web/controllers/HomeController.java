package com.revenat.jcart.admin.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController extends JCartAdminBaseController {

    @RequestMapping("/home")
    public String home(Model model) {
        return "home";
    }
}
