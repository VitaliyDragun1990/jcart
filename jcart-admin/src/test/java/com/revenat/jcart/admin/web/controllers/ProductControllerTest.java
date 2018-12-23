package com.revenat.jcart.admin.web.controllers;

import com.revenat.config.MockSecurityServiceConfig;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartAdminApplication;
import com.revenat.jcart.admin.web.controllers.builders.CategoryBuilder;
import com.revenat.jcart.admin.web.controllers.builders.ProductBuilder;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.common.services.ImageService;
import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.entities.Product;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

import static org.mockito.Matchers.anyString;
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
public class ProductControllerTest {

    private static final String AUTH_USER_EMAIL = "john@gmail.com";

    private static final String HEADER_TITLE = "Manage Products";
    private static final Integer CATEGORY_ID = 1;
    private static final String CATEGORY_NAME = "Category_A";
    private static final String CATEGORY_DESCRIPTION = "Category description";
    private static final Integer PRODUCT_ID = 1;
    private static final String PRODUCT_NAME = "Product_A";
    private static final String PRODUCT_DESCRIPTION = "Product description";
    private static final String PRODUCT_SKU = "Product SKU";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.ONE;
    private static final String VIEW_PRODUCTS = "products/products";
    private static final String VIEW_CREATE_PRODUCT = "products/create_product";
    private static final String VIEW_EDIT_PRODUCT = "products/edit_product";
    private static final String VIEW_404 = "error/404";
    private static final String VIEW_500 = "error/500";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private ProductController controller;

    @Autowired
    private ImageService imageService;
    @Autowired
    private CatalogService catalogService;

    @Before
    public void setUpMocks() {
        Mockito.reset(imageService);
        Mockito.reset(catalogService);
    }

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setUpController() {
        controller = context.getBean("productController", ProductController.class);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void getHeaderTitle_ReturnsHeaderTitle() {
        String headerTitle = controller.getHeaderTitle();

        assertThat(headerTitle, equalTo(HEADER_TITLE));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void categoriesList_ShouldAddCategoryEntriesToModel() {
        Category category = new CategoryBuilder()
                .withId(CATEGORY_ID)
                .withName(CATEGORY_NAME)
                .withDescription(CATEGORY_DESCRIPTION)
                .build();
        when(catalogService.getAllCategories()).thenReturn(Arrays.asList(category));

        List<Category> allCategories = controller.categoriesList();

        assertThat(allCategories, hasSize(1));
        verify(catalogService, times(1)).getAllCategories();
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void listProducts_ShouldAddProductEntriesToModelAndRenderProductsView() throws Exception {
        Product product = new ProductBuilder()
                .withId(PRODUCT_ID)
                .withName(PRODUCT_NAME)
                .withDescription(PRODUCT_DESCRIPTION)
                .build();
        when(catalogService.getAllProducts()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", hasSize(1)))
                .andExpect(model().attribute("products", hasItem(
                        allOf(
                                hasProperty("id", is(PRODUCT_ID)),
                                hasProperty("name", is(PRODUCT_NAME)),
                                hasProperty("description", is(PRODUCT_DESCRIPTION))
                        )
                )))
                .andExpect(view().name(VIEW_PRODUCTS));

        verify(catalogService, times(1)).getAllProducts();
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createProductForm_ShouldAddProductEntryToModelAndRenderCreateProductView() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(view().name(VIEW_CREATE_PRODUCT));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createProduct_NewProductEntry_ShouldCreateProductAndRenderProductsView() throws Exception {
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        when(catalogService.getProductBySku(anyString())).thenReturn(null);
        when(catalogService.createProduct(Matchers.isA(Product.class))).thenAnswer(
                (Answer<Product>) invocationOnMock -> (Product) invocationOnMock.getArguments()[0]);

        mockMvc.perform(post("/products")
                .param("sku", PRODUCT_SKU)
                .param("name", PRODUCT_NAME)
                .param("price", PRODUCT_PRICE.toString())
                .param("categoryId", CATEGORY_ID.toString()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/products"));

        verify(catalogService, times(1)).createProduct(productCaptor.capture());
        Product createdProduct = productCaptor.getValue();
        assertThat(createdProduct.getName(), equalTo(PRODUCT_NAME));
        assertThat(createdProduct.getSku(), equalTo(PRODUCT_SKU));
        assertThat(createdProduct.getPrice(), equalTo(PRODUCT_PRICE));
        assertThat(createdProduct.getCategory().getId(), equalTo(CATEGORY_ID));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createProduct_EmptySkuAndName_ShouldRenderCreateProductViewAndReturnValidationErrors() throws Exception {
        mockMvc.perform(post("/products")
                .param("name", "")
                .param("categoryId", CATEGORY_ID.toString())
                .param("price", PRODUCT_PRICE.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(model().hasErrors()).andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrors("product", "sku", "name"))
                .andExpect(view().name(VIEW_CREATE_PRODUCT));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void createProduct_SkuNameAlreadyTaken_ShouldRenderCreateProductViewAndReturnValidationErrors() throws Exception {
        when(catalogService.getProductBySku(PRODUCT_SKU)).thenReturn(new ProductBuilder().build());

        mockMvc.perform(post("/products")
                .param("name", PRODUCT_NAME)
                .param("sku", PRODUCT_SKU)
                .param("price", PRODUCT_PRICE.toString())
                .param("categoryId", CATEGORY_ID.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(model().hasErrors()).andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("product", "sku"))
                .andExpect(view().name(VIEW_CREATE_PRODUCT));

        verify(catalogService, times(1)).getProductBySku(PRODUCT_SKU);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editProductForm_ProductFound_ShouldAddProductEntryToModelAndRenderEditProductView() throws Exception {
        Product product = new ProductBuilder()
                .withId(PRODUCT_ID)
                .withName(PRODUCT_NAME)
                .withDescription(PRODUCT_DESCRIPTION)
                .withSku(PRODUCT_SKU)
                .withPrice(PRODUCT_PRICE)
                .withCategory(
                        new CategoryBuilder().withId(CATEGORY_ID).build()
                ).build();
        when(catalogService.getProductById(PRODUCT_ID)).thenReturn(product);

        mockMvc.perform(get("/products/"+PRODUCT_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", allOf(
                        hasProperty("id", is(PRODUCT_ID)),
                        hasProperty("name", is(PRODUCT_NAME)),
                        hasProperty("description", is(PRODUCT_DESCRIPTION)),
                        hasProperty("price", is(PRODUCT_PRICE)),
                        hasProperty("sku", is(PRODUCT_SKU))
                )))
                .andExpect(view().name(VIEW_EDIT_PRODUCT));

        verify(catalogService, times(1)).getProductById(PRODUCT_ID);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void editProductForm_ProductNotFound_ShouldAddExceptionToModelAndRender404View() throws Exception {
        when(catalogService.getProductById(Matchers.any(Integer.class))).thenReturn(null);

        mockMvc.perform(get("/products/"+PRODUCT_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("exception"))
                .andExpect(view().name(VIEW_404));

        verify(catalogService, times(1)).getProductById(PRODUCT_ID);
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void showProductImage_ShouldLoadProductImage() throws Exception {
        mockMvc.perform(get("/products/images/"+PRODUCT_ID))
                .andDo(print());

        verify(imageService, times(1)).loadImage("1.jpg");
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateProduct_UpdatedProductEntry_ShouldUpdateProductAndRenderProductsView() throws Exception {
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        when(catalogService.updateProduct(Matchers.any(Product.class))).thenAnswer(
                (Answer<Product>) invocationOnMock -> (Product) invocationOnMock.getArguments()[0]);

        mockMvc.perform(post("/products/"+PRODUCT_ID)
                .param("sku", PRODUCT_SKU)
                .param("name", PRODUCT_NAME)
                .param("price", PRODUCT_PRICE.toString())
                .param("categoryId", CATEGORY_ID.toString()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("info"))
                .andExpect(redirectedUrl("/products"));

        verify(catalogService, times(1)).updateProduct(productCaptor.capture());
        Product updatedProduct = productCaptor.getValue();
        assertThat(updatedProduct.getSku(), equalTo(PRODUCT_SKU));
        assertThat(updatedProduct.getName(), equalTo(PRODUCT_NAME));
        assertThat(updatedProduct.getPrice(), equalTo(PRODUCT_PRICE));
        assertThat(updatedProduct.getCategory().getId(), equalTo(CATEGORY_ID));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateProduct_EmptyPrice_ShouldRenderEditProductViewAndReturnValidationErrors() throws Exception {
        mockMvc.perform(post("/products/"+PRODUCT_ID)
                .param("id", PRODUCT_ID.toString())
                .param("sku", PRODUCT_SKU)
                .param("name", PRODUCT_NAME)
                .param("categoryId", CATEGORY_ID.toString())
                .param("price", "0.0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("product"))
                .andExpect(model().hasErrors()).andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("product", "price"))
                .andExpect(view().name(VIEW_EDIT_PRODUCT));

        verify(catalogService, times(0)).updateProduct(Matchers.any(Product.class));
    }

    @Test
    @WithUserDetails(AUTH_USER_EMAIL)
    public void updateProduct_ProductNotFound_ShouldAddExceptionToModelAndRender500View() throws Exception {
        when(catalogService.updateProduct(Matchers.any(Product.class))).thenThrow(new JCartException());

        mockMvc.perform(post("/products/"+PRODUCT_ID)
                .param("sku", PRODUCT_SKU)
                .param("name", PRODUCT_NAME)
                .param("price", PRODUCT_PRICE.toString())
                .param("categoryId", CATEGORY_ID.toString()))
                .andDo(print())
                .andExpect(model().attributeExists("exception"))
                .andExpect(status().is5xxServerError())
                .andExpect(view().name(VIEW_500));
    }
}