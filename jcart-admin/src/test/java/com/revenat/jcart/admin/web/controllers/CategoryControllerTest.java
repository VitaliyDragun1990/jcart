package com.revenat.jcart.admin.web.controllers;

import com.revenat.config.MockSecurityServiceConfig;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.admin.web.controllers.builders.CategoryBuilder;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.exceptions.JCartException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartAdminApplication.class, TestConfig.class, MockSecurityServiceConfig.class}
        )
@WebAppConfiguration
@ActiveProfiles("test")
public class CategoryControllerTest {

    private static final String AUTH_USER_EMAIL = "john@gmail.com";

    private static final String HEADER_TITLE = "Manage Categories";
    private static final Integer CATEGORY_ID = 1;
    private static final String CATEGORY_NAME = "Category_A";
    private static final String CATEGORY_DESCRIPTION = "Category_Description";
    private static final Integer CATEGORY_DISPLAY_ORDER = 1;
    private static final String VIEW_CATEGORIES = "categories/categories";
    private static final String VIEW_CREATE_CATEGORY = "categories/create_category";
    private static final String VIEW_EDIT_CATEGORY = "categories/edit_category";
    private static final String VIEW_404 = "error/404";
    private static final String VIEW_500 = "error/500";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CatalogService catalogService;

    private MockMvc mockMvc;
    private CategoryController controller;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpMocks() {
        Mockito.reset(catalogService);
    }

    @Before
    public void setUpController() {
        controller = context.getBean("categoryController", CategoryController.class);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void getHeaderTitle_ReturnsHeaderTitle() {
        assertThat(controller.getHeaderTitle(), equalTo(HEADER_TITLE));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void listCategories_ShouldAddCategoryEntriesToModelAndRenderCategoryView() throws Exception {
        Category category = new CategoryBuilder()
                .withId(CATEGORY_ID)
                .withName(CATEGORY_NAME)
                .withDescription(CATEGORY_DESCRIPTION)
                .build();
        when(catalogService.getAllCategories()).thenReturn(Arrays.asList(category));

        mockMvc.perform(get("/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("categories", hasSize(CATEGORY_ID)))
                .andExpect(model().attribute("categories", hasItem(
                        allOf(
                                hasProperty("id", is(CATEGORY_ID)),
                                hasProperty("name", is(CATEGORY_NAME)),
                                hasProperty("description", is(CATEGORY_DESCRIPTION))
                        )
                )))
                .andExpect(view().name(VIEW_CATEGORIES));

        verify(catalogService, times(CATEGORY_ID)).getAllCategories();
        verifyNoMoreInteractions(catalogService);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createCategoryForm_ShouldAddCategoryEntryToModelAndRenderCreateCategoryView() throws Exception {
        mockMvc.perform(get("/categories/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("category"))
                .andExpect(view().name(VIEW_CREATE_CATEGORY));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createCategory_NewCategoryEntry_ShouldCreateCategoryAndRenderCategoriesView() throws Exception {
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        when(catalogService.getCategoryByName(anyString())).thenReturn(null);
        when(catalogService.createCategory(Matchers.isA(Category.class))).thenAnswer(
                (Answer<Category>) invocationOnMock -> (Category) invocationOnMock.getArguments()[0]);

        mockMvc.perform(post("/categories")
                .param("name", CATEGORY_NAME)
                .param("description", CATEGORY_DESCRIPTION)
                .param("displayOrder", CATEGORY_DISPLAY_ORDER.toString()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/categories"));

        verify(catalogService, times(1)).createCategory(categoryCaptor.capture());
        Category persistedCategory = categoryCaptor.getValue();
        assertThat(persistedCategory.getName(), equalTo(CATEGORY_NAME));
        assertThat(persistedCategory.getDescription(), equalTo(CATEGORY_DESCRIPTION));
        assertThat(persistedCategory.getDisplayOrder(), equalTo(CATEGORY_DISPLAY_ORDER));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createCategory_EmptyName_ShouldRenderCreateCategoryViewAndReturnValidationErrors() throws Exception {
        mockMvc.perform(post("/categories")
                .param("name", "")
                .param("description", CATEGORY_DESCRIPTION)
                .param("displayOrder", CATEGORY_DISPLAY_ORDER.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("category"))
                .andExpect(model().errorCount(CATEGORY_ID))
                .andExpect(model().attributeHasFieldErrors("category", "name"))
                .andExpect(view().name(VIEW_CREATE_CATEGORY));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createCategory_NameAlreadyTaken_ShouldRenderCreateCategoryViewAndReturnValidationErrors() throws Exception {
        Category category = new CategoryBuilder().withName(CATEGORY_NAME).build();
        when(catalogService.getCategoryByName(anyString())).thenReturn(category);

        mockMvc.perform(post("/categories")
                .param("name", CATEGORY_NAME)
                .param("description", CATEGORY_DESCRIPTION)
                .param("displayOrder", CATEGORY_DISPLAY_ORDER.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("category"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("category", "name"))
                .andExpect(view().name(VIEW_CREATE_CATEGORY));

        verify(catalogService, times(1)).getCategoryByName(anyString());
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editCategoryForm_ValidCategoryId_ShouldAddCategoryEntryToModelAndRenderEditCategoryView() throws Exception {
        Category category = new CategoryBuilder()
                .withId(CATEGORY_ID)
                .withName(CATEGORY_NAME)
                .withDescription(CATEGORY_DESCRIPTION)
                .build();
        when(catalogService.getCategoryById(CATEGORY_ID)).thenReturn(category);

        mockMvc.perform(get("/categories/"+CATEGORY_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attribute("category", allOf(
                        hasProperty("id", is(CATEGORY_ID)),
                        hasProperty("name", is(CATEGORY_NAME)),
                        hasProperty("description", is(CATEGORY_DESCRIPTION))
                )))
                .andExpect(view().name(VIEW_EDIT_CATEGORY));

        verify(catalogService, times(1)).getCategoryById(CATEGORY_ID);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editCategoryForm_CategoryNotFound_ShouldAddExceptionToModelAndRender404View() throws Exception {
        when(catalogService.getCategoryById(Matchers.any(Integer.class))).thenReturn(null);

        mockMvc.perform(get("/categories/"+CATEGORY_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("exception"))
                .andExpect(view().name(VIEW_404));

        verify(catalogService, times(1)).getCategoryById(CATEGORY_ID);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateCategory_UpdatedCategoryEntry_ShouldUpdateCategoryAndRenderCategoriesView() throws Exception {
        when(catalogService.updateCategory(Matchers.isA(Category.class))).thenAnswer(
                (Answer<Category>) invocationOnMock -> (Category) invocationOnMock.getArguments()[0]);
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        mockMvc.perform(post("/categories/"+CATEGORY_ID)
                .param("id", CATEGORY_ID.toString())
                .param("name", CATEGORY_NAME)
                .param("description", CATEGORY_DESCRIPTION))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/categories"));

        verify(catalogService, times(1)).updateCategory(categoryCaptor.capture());
        Category updatedCategory = categoryCaptor.getValue();
        assertThat(updatedCategory.getId(), equalTo(CATEGORY_ID));
        assertThat(updatedCategory.getName(), equalTo(CATEGORY_NAME));
        assertThat(updatedCategory.getDescription(), equalTo(CATEGORY_DESCRIPTION));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateCategory_CategoryNotFound_ShouldAddExceptionToModelRender500View() throws Exception {
        when(catalogService.updateCategory(Matchers.isA(Category.class))).thenThrow(new JCartException());

        mockMvc.perform(post("/categories/"+CATEGORY_ID)
                .param("name", CATEGORY_NAME)
                .param("description", CATEGORY_DESCRIPTION))
                .andDo(print())
                .andExpect(model().attributeExists("exception"))
                .andExpect(status().is5xxServerError())
                .andExpect(view().name(VIEW_500));

        verify(catalogService, times(1)).updateCategory(Matchers.any(Category.class));
    }
}