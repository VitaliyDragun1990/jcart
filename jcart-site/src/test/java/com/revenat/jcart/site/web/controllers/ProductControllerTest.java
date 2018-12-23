package com.revenat.jcart.site.web.controllers;

import com.revenat.builders.CategoryBuilder;
import com.revenat.builders.ProductBuilder;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartSiteApplication;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.common.services.ImageService;
import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.entities.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartSiteApplication.class, TestConfig.class}
)
@WebAppConfiguration
@ActiveProfiles("test")
public class ProductControllerTest {
    private static final Integer PRODUCT_ID = 1;
    private static final String PRODUCT_NAME = "Product name";
    private static final String PRODUCT_SKU = "test_sku";
    private static final String VIEW_PRODUCTS = "public/products";
    private static final String SEARCH_QUERY = "Toy";
    private static final String VIEW_PRODUCT = "public/product";
    private static final int CATEGORY_ID = 1;
    private static final String CATEGORY_NAME = "Category name";
    private static final byte[] IMAGE_CONTENT = {1, 2, 3};
    private static final String VIEW_NOT_FOUND = "error/404";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ProductController productController;

    @Autowired
    private ImageService imageService;
    @Autowired
    private CatalogService catalogService;

    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpMocks() {
        Mockito.reset(imageService, catalogService);
    }

    @Test
    public void getHeaderTitle_ShouldReturnHeaderTitle() {
        String headerTitle = productController.getHeaderTitle();

        assertThat(headerTitle, equalTo("Product"));
    }

    @Test
    public void searchProducts_SearchQuery_ShouldAddProductEntriesToModelAndRenderProductsView() throws Exception {
        Product product = new ProductBuilder().withId(PRODUCT_ID).withName(PRODUCT_NAME).withSku(PRODUCT_SKU).build();
        when(catalogService.searchProduct(anyString())).thenReturn(Arrays.asList(product));

        MockHttpServletResponse response = mockMvc.perform(get("/products")
                .param("q", SEARCH_QUERY))
                .andDo(print())
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", hasSize(1)))
                .andExpect(model().attribute("products", hasItem(
                        allOf(
                                hasProperty("id", is(PRODUCT_ID)),
                                hasProperty("name", is(PRODUCT_NAME)),
                                hasProperty("sku", is(PRODUCT_SKU))
                        )
                )))
                .andExpect(view().name(VIEW_PRODUCTS))
                .andReturn().getResponse();

        verify(catalogService, times(1)).searchProduct(SEARCH_QUERY);
        assertThat(response.getStatus(), equalTo(HttpStatus.OK.value()));
    }

    @Test
    public void getProductPage_ProductFound_ShouldAddProductEntryToModelAndRenderProductView() throws Exception {
        Category category = new CategoryBuilder().withId(CATEGORY_ID).withName(CATEGORY_NAME).build();
        Product product = new ProductBuilder().withId(PRODUCT_ID).withName(PRODUCT_NAME)
                .withSku(PRODUCT_SKU).withCategory(category).build();
        when(catalogService.getProductBySku(PRODUCT_SKU)).thenReturn(product);

        mockMvc.perform(get("/products/" + PRODUCT_SKU))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product",
                        allOf(
                                hasProperty("id", is(PRODUCT_ID)),
                                hasProperty("name", is(PRODUCT_NAME)),
                                hasProperty("sku", is(PRODUCT_SKU)),
                                hasProperty("category", allOf(
                                        hasProperty("id", is(CATEGORY_ID)),
                                        hasProperty("name", is(CATEGORY_NAME))
                                ))
                        )
                ))
                .andExpect(view().name(VIEW_PRODUCT));

        verify(catalogService, times(1)).getProductBySku(PRODUCT_SKU);
    }

    @Test
    public void getProductPage_ProductNotFound_ShouldAddExceptionEntryToModel() throws Exception {
        when(catalogService.getProductBySku(anyString())).thenReturn(null);

        MvcResult result = mockMvc.perform(get("/products/" + PRODUCT_SKU)).andReturn();

        ModelMap model = result.getModelAndView().getModelMap();
        assertThat(model, hasKey(is("exception")));
        verify(catalogService).getProductBySku(anyString());
    }

    @Test
    public void getProductPage_ProductNotFound_ShouldReturnStatusNotFound() throws Exception {
        when(catalogService.getProductBySku(anyString())).thenReturn(null);

        mockMvc.perform(get("/products/"+PRODUCT_SKU))
                .andExpect(status().isNotFound());

        verify(catalogService).getProductBySku(anyString());
    }

    @Test
    public void getProductPage_ProductNotFound_ShouldRenderNotFoundView() throws Exception {
        when(catalogService.getProductBySku(anyString())).thenReturn(null);

        mockMvc.perform(get("/products/"+PRODUCT_SKU))
                .andExpect(view().name(VIEW_NOT_FOUND));

        verify(catalogService).getProductBySku(anyString());
    }

    @Test
    public void showProductImage_ShouldLoadProductImage() throws Exception {
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        when(imageService.loadImage(anyString())).thenReturn(IMAGE_CONTENT);

        mockMvc.perform(get("/products/images/" + PRODUCT_ID))
                .andDo(print());

        verify(imageService, times(1)).loadImage(stringCaptor.capture());
        String imageName = stringCaptor.getValue();
        assertThat(imageName, containsString(PRODUCT_ID.toString()));
    }
}