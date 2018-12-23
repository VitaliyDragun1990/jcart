package com.revenat.jcart.site.web.controllers;

import com.revenat.builders.CategoryBuilder;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartSiteApplication;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Category;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartSiteApplication.class, TestConfig.class}
        )
@WebAppConfiguration
@ActiveProfiles("test")
public class HomeControllerTest {

    private static final Integer CATEGORY_ID = 1;
    private static final String CATEGORY_NAME = "Category name";
    private static final String HEADER_TITLE = "Home";
    private static final String VIEW_HOME = "public/home";
    private static final String VIEW_CATEGORY = "public/category";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private HomeController homeController;

    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpMocks() {
        Mockito.reset(catalogService);
    }

    @Test
    public void getHeaderTitle_ReturnsHeaderTitle() {
        String headerTitle = homeController.getHeaderTitle();

        assertThat(headerTitle, equalTo(HEADER_TITLE));
    }

    @Test
    public void home_ShouldAddCategoryEntriesToModelAndRenderHomeView() throws Exception {
        Category category = new CategoryBuilder().build();
        when(catalogService.getAllCategories()).thenReturn(Arrays.asList(category));

        mockMvc.perform(get("/home"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("categories", hasSize(1)))
                .andExpect(view().name(VIEW_HOME));

        verify(catalogService, times(1)).getAllCategories();
    }

    @Test
    public void category_CategoryFound_ShouldAddCategoryEntryToModelAndRenderCategoryView() throws Exception {
        Category category = new CategoryBuilder().withId(CATEGORY_ID).withName(CATEGORY_NAME).build();
        when(catalogService.getCategoryByName(CATEGORY_NAME)).thenReturn(category);

        mockMvc.perform(get("/categories/"+CATEGORY_NAME))
                .andDo(print())
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attribute("category",
                        allOf(
                                hasProperty("id", equalTo(CATEGORY_ID)),
                                hasProperty("name", equalTo(CATEGORY_NAME))
                        )
                ))
                .andExpect(view().name(VIEW_CATEGORY));

        verify(catalogService, times(1)).getCategoryByName(CATEGORY_NAME);
    }

    @Test
    public void category_categoryNotFound_ShouldRenderNotFoundView() throws Exception {
        when(catalogService.getCategoryByName(CATEGORY_NAME)).thenReturn(null);

        mockMvc.perform(get("/categories/"+CATEGORY_NAME))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("exception"))
                .andExpect(view().name("error/404"));
    }
}