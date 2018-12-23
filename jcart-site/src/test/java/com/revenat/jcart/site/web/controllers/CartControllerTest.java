package com.revenat.jcart.site.web.controllers;

import com.revenat.builders.ProductBuilder;
import com.revenat.config.MockCatalogServiceConfig;
import com.revenat.config.MockCustomerServiceConfig;
import com.revenat.config.MockImageServiceConfig;
import com.revenat.config.TestConfig;
import com.revenat.jcart.JCartSiteApplication;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Product;
import com.revenat.jcart.site.web.models.Cart;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {JCartSiteApplication.class, TestConfig.class}
)
@WebAppConfiguration
@ActiveProfiles("test")
public class CartControllerTest {
    private static final String PRODUCT_SKU = "P1001";
    private static final String CART_KEY = JCartSiteBaseController.CART_KEY;
    private static final String APPLICATION_JSON = "application/json";
    private static final String HEADER_TITLE = "Cart";
    private static final String VIEW_CART = "public/cart";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CartController controller;
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
        Mockito.reset(catalogService);
    }

    @Test
    public void getHeaderTitle_ReturnsHeaderTitle() {
        String headerTitle = controller.getHeaderTitle();

        assertThat(headerTitle, equalTo(HEADER_TITLE));
    }

    @Test
    public void showCart_ShouldAddCartToModelAndRenderCartView() throws Exception {
        mockMvc.perform(get("/cart"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cart"))
                .andExpect(view().name(VIEW_CART));
    }

    @Test
    public void getCartItemCount_ReturnsCartItemCountAsJson() throws Exception {
        mockMvc.perform(get("/cart/items/count").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.count", is(0)));
    }

    @Test
    public void addToCart_ProductFound_ShouldAddItemToCart() throws Exception {
        Product product = new ProductBuilder().withSku(PRODUCT_SKU).build();
        when(catalogService.getProductBySku(PRODUCT_SKU)).thenReturn(product);
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));

        mockMvc.perform(post("/cart/items").sessionAttr(CART_KEY, cart)
                .content("{\"sku\": \"" + PRODUCT_SKU + "\"}")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(catalogService, times(1)).getProductBySku(PRODUCT_SKU);
        assertThat(cart.getItemCount(), equalTo(2));
    }

    @Test
    public void addToCart_ProductNotFound_ShouldNotChangeAddProductToCart() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));
        when(catalogService.getProductBySku(PRODUCT_SKU)).thenReturn(null);

        mockMvc.perform(post("/cart/items")
                .content("{\"sku\": \"" + PRODUCT_SKU + "\"}")
                .contentType(APPLICATION_JSON))
                .andDo(print());

        assertThat(cart.getItemCount(), equalTo(1));
        verify(catalogService, times(1)).getProductBySku(PRODUCT_SKU);
    }

    @Test
    public void addToCart_ProductNotFound_ShouldReturnNotFound() throws Exception {
        when(catalogService.getProductBySku(PRODUCT_SKU)).thenReturn(null);

        MockHttpServletResponse response = mockMvc.perform(post("/cart/items")
                .content("{\"sku\": \"" + PRODUCT_SKU + "\"}")
                .contentType(APPLICATION_JSON))
                .andDo(print()).andReturn().getResponse();

        verify(catalogService, times(1)).getProductBySku(PRODUCT_SKU);
        assertThat(response.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
        assertThat(response.getContentAsString(), containsString(PRODUCT_SKU));
    }

    @Test
    public void updateCartItem_PositiveQuantity_ShouldUpdateCartItemQuantity() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));
        int newQuantity = 10;

        mockMvc.perform(put("/cart/items").sessionAttr(CART_KEY, cart)
                .content("{\"product\": {\"sku\": \"" + PRODUCT_SKU + "\"}, \"quantity\": " + newQuantity + " }")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(cart.getItemCount(), equalTo(newQuantity));
    }

    @Test
    public void updateCartItem_NegativeQuantity_ShouldDeleteItemFromCart() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));
        int newQuantity = -1;

        mockMvc.perform(put("/cart/items").sessionAttr(CART_KEY, cart)
                .content("{\"product\": {\"sku\": \"" + PRODUCT_SKU + "\"}, \"quantity\": " + newQuantity + " }")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(cart.getItemCount(), equalTo(0));
    }

    @Test
    public void removeCartItem_ValidProductSku_ShouldRemoveProductWithSkuFromCart() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));

        mockMvc.perform(delete("/cart/items/" + PRODUCT_SKU).sessionAttr(CART_KEY, cart)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(cart.getItemCount(), equalTo(0));
    }

    @Test
    public void removeCartItem_SkuForProductNotInCart_ShouldNotChangedCart() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));
        String unknownSKu = "dummy";

        mockMvc.perform(delete("/cart/items/" + unknownSKu).sessionAttr(CART_KEY, cart)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(cart.getItemCount(), equalTo(1));
    }

    @Test
    public void clearCart_ShouldRemoveAllProductsFromCart() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));

        mockMvc.perform(delete("/cart/").sessionAttr(CART_KEY, cart)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(cart.getItemCount(), equalTo(0));
    }

    private static class CartBuilder {
        private Cart cart;

        private CartBuilder() {
            cart = new Cart();
        }

        static CartBuilder buildCart() {
            return new CartBuilder();
        }

        CartBuilder withItem(String productSku) {
            Product product = new Product();
            product.setSku(productSku);
            cart.addItem(product);
            return this;
        }

        Cart build() {
            return cart;
        }
    }
}