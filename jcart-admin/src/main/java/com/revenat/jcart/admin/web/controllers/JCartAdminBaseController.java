package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.admin.security.AuthenticatedUser;
import com.revenat.jcart.core.common.services.JCLogger;
import com.revenat.jcart.core.exceptions.JCartException;
import com.revenat.jcart.core.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

public abstract class JCartAdminBaseController {

    protected final JCLogger LOGGER = JCLogger.getLogger(getClass());

    @Autowired
    protected MessageSource messageSource;

    @ModelAttribute("headerTitle")
    protected abstract String getHeaderTitle();

    protected String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    protected String getMessage(String code, String defaultMsg) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, defaultMsg, locale);
    }

    @ModelAttribute("authenticatedUser")
    public AuthenticatedUser authenticatedUser(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return authenticatedUser;
    }

    protected static AuthenticatedUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AuthenticatedUser) {
            return (AuthenticatedUser) principal;
        }
        // If principal not AuthenticatedUser,it is either null or represents anonymous user -
        // neither of which our domain User object can represent - so return null
        return null;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NotFoundException exc) {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("exception", exc);
        return modelAndView;
    }

    @ExceptionHandler(JCartException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleJCartException(JCartException exc) {
        ModelAndView modelAndView = new ModelAndView("error/500");
        modelAndView.addObject("exception",exc);
        return modelAndView;
    }

    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }
}
