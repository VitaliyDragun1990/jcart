package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.core.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = {HomeController.class, ProductController.class})
public class ViewExceptionProcessor {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException exc, Model model) {
        model.addAttribute("exception", exc);
        return "error/404";
    }

    @ExceptionHandler(JCartException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleJCartException(JCartException exc, Model model) {
        model.addAttribute("exception", exc);
        return "error/500";
    }
}
