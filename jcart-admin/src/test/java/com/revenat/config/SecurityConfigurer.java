package com.revenat.config;

import com.revenat.jcart.admin.security.SecurityUtil;
import com.revenat.jcart.admin.web.controllers.builders.RoleBuilder;
import com.revenat.jcart.admin.web.controllers.builders.UserBuilder;
import com.revenat.jcart.core.entities.User;
import com.revenat.jcart.core.security.SecurityService;

import static org.mockito.Mockito.when;

public final class SecurityConfigurer {

    public static void configureUserWithFullAuthorities(SecurityService securityService, String userEmail) {
        when(securityService.findUserByEmail(userEmail)).thenReturn(createUserWithAuthorities(userEmail));
    }

    public static void configureUserWithoutAuthorities(SecurityService securityService, String userEmail) {
        when(securityService.findUserByEmail(userEmail)).thenReturn(createUserWithoutAuthorities(userEmail));
    }

    private static User createUserWithAuthorities(String userEmail) {
        return UserBuilder.getBuilder()
                .withEmail(userEmail)
                .withName("user")
                .withPassword("password")
                .withRole(RoleBuilder.getBuilder().withName(SecurityUtil.MANAGE_PERMISSIONS).build())
                .withRole(RoleBuilder.getBuilder().withName(SecurityUtil.MANAGE_ROLES).build())
                .withRole(RoleBuilder.getBuilder().withName(SecurityUtil.MANAGE_USERS).build())
                .withRole(RoleBuilder.getBuilder().withName(SecurityUtil.MANAGE_CATEGORIES).build())
                .withRole(RoleBuilder.getBuilder().withName(SecurityUtil.MANAGE_PRODUCTS).build())
                .withRole(RoleBuilder.getBuilder().withName(SecurityUtil.MANAGE_ORDERS).build())
                .withRole(RoleBuilder.getBuilder().withName(SecurityUtil.MANAGE_CUSTOMERS).build())
                .build();
    }

    private static User createUserWithoutAuthorities(String userEmail) {
        return UserBuilder.getBuilder()
                .withEmail(userEmail)
                .withName("user")
                .withPassword("password")
                .build();
    }
}
