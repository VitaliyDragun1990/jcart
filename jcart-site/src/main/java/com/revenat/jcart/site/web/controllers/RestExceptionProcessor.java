package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.exceptions.NotFoundException;
import com.revenat.jcart.site.web.exceptions.JsonError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = CartController.class)
public class RestExceptionProcessor {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public JsonError handleNotFound(NotFoundException ex) {
        return new JsonError(ex.getMessage());
    }
}
