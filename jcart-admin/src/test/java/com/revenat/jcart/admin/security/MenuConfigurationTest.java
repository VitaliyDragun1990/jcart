package com.revenat.jcart.admin.security;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class MenuConfigurationTest {
    private static final String HOME_URI = "/home?order=10";
    private static final String CATEGORIES_URI = "/categories";
    private static final String USERS_URI = "/users?id=12";
    private static final String ROLES_URI = "/roles?id=12&permission=true";

    @Test
    public void testMatchingMenu() {
        String homeMenu = MenuConfiguration.getMatchingMenu(HOME_URI);
        String categoriesMenu = MenuConfiguration.getMatchingMenu(CATEGORIES_URI);
        String usersMenu = MenuConfiguration.getMatchingMenu(USERS_URI);
        String rolesMenu = MenuConfiguration.getMatchingMenu(ROLES_URI);

        assertThat(homeMenu, equalTo("Home"));
        assertThat(categoriesMenu, equalTo("Categories"));
        assertThat(usersMenu, equalTo("Users"));
        assertThat(rolesMenu, equalTo("Roles"));
    }
}