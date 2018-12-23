package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.core.common.services.JCLogger;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminErrorController implements ErrorController {
    private static final JCLogger LOGGER = JCLogger.getLogger(AdminErrorController.class);

    private static final String VIEW_PATH = "error/";
    private static final String ERROR_PATH = "/error";
    private static final String NOT_FOUND = "404";
    private static final String SERVER_ERROR = "500";

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            LOGGER.error("Error occurred during processing user's request. Error status code: {}.", status.toString());
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return VIEW_PATH + NOT_FOUND;
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return VIEW_PATH + SERVER_ERROR;
            }
        }
        return VIEW_PATH + SERVER_ERROR;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
