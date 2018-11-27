package com.revenat.jcart.admin.web.controllers;

import com.revenat.jcart.admin.security.AuthenticatedUser;
import com.revenat.jcart.common.services.JCLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class JCartAdminBaseController {

    protected static final JCLogger LOGGER = JCLogger.getLogger(JCartAdminBaseController.class);

    @Autowired
    protected MessageSource messageSource;

    @ModelAttribute("headerTitle")
    protected abstract String getHeaderTitle();

    protected String getMessage(String code) {
        return messageSource.getMessage(code, null, null);
    }

    public String getMessage(String code, String defaultMsg) {
        return messageSource.getMessage(code, null, defaultMsg, null);
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

    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }
}
