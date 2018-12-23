package com.revenat.jcart.site.web.controllers;

import com.revenat.jcart.core.common.services.JCLogger;
import com.revenat.jcart.site.security.AuthenticatedCustomer;
import com.revenat.jcart.site.web.models.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;


import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

abstract class JCartSiteBaseController {

    static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    static final String CART_KEY = "CART_KEY";
    final JCLogger logger = JCLogger.getLogger(getClass());

    @Autowired
    private MessageSource messageSource;

    abstract String getHeaderTitle();

    String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    String getMessage(String code, String defaultMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, defaultMessage, locale);
    }

    @ModelAttribute("headerTitle")
    String headerTitle() {
        return getHeaderTitle();
    }

    @ModelAttribute("authenticatedUser")
    AuthenticatedCustomer authenticatedCustomer(@AuthenticationPrincipal AuthenticatedCustomer authenticatedCustomer) {
        return authenticatedCustomer;
    }

    static AuthenticatedCustomer getCurrentCustomer() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AuthenticatedCustomer) {
            return ((AuthenticatedCustomer) principal);
        }
        // principal object is either null or represents anonymous user -
        // neither of which our domain Customer object can represent - so return null
        return null;
    }

    static boolean isLoggedIn() {
        return getCurrentCustomer() != null;
    }

    Cart getOrCreateCart(HttpServletRequest request) {
        Cart cart;
        cart = (Cart) request.getSession().getAttribute(CART_KEY);
        if (cart == null) {
            cart = new Cart();
            request.getSession().setAttribute(CART_KEY, cart);
        }
        return cart;
    }
}
