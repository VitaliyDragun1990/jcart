package com.revenat.jcart.site.web.controllers;

import com.revenat.config.MockImageServiceConfig;
import com.revenat.jcart.JCartSiteApplication;
import com.revenat.jcart.core.entities.Product;
import com.revenat.jcart.site.web.models.Cart;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({JCartSiteApplication.class, MockImageServiceConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class CartControllerTest {
    private static final String PRODUCT_SKU = "P1001";
    private static final String CART_KEY = JCartSiteBaseController.CART_KEY;
    private static final String APPLICATION_JSON = "application/json";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CartController controller;

    private MockMvc mockMvc;

    @Before
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getTitle() {
        String headerTitle = controller.getHeaderTitle();

        assertThat(headerTitle, equalTo("Cart"));
    }

    @Test
    public void showCartPage() throws Exception {
        mockMvc.perform(get("/cart"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cart"))
                .andExpect(view().name("public/cart"));
    }

    @Test
    public void getCartItemCount() throws Exception {
        mockMvc.perform(get("/cart/items/count").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.count", is(0)));
    }

    @Test
    public void addToCart() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));

        mockMvc.perform(post("/cart/items").sessionAttr(CART_KEY, cart)
                .content("{\"sku\": \"" + PRODUCT_SKU + "\"}")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        assertThat(cart.getItemCount(), equalTo(2));
    }

    @Test
    public void addToCart_ProductNotFound() throws Exception {
        String badSku = "badSku";

        mockMvc.perform(post("/cart/items")
                .content("{\"sku\": \"" + badSku + "\"}")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(badSku)));
    }

    @Test
    public void updateCartItemQuantity() throws Exception {
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
    public void updateCartItemQuantity_DeleteItem() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));
        int newQuantity = 0;

        mockMvc.perform(put("/cart/items").sessionAttr(CART_KEY, cart)
                .content("{\"product\": {\"sku\": \"" + PRODUCT_SKU + "\"}, \"quantity\": " + newQuantity + " }")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(cart.getItemCount(), equalTo(newQuantity));
    }

    @Test
    public void removeCartItem() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem(PRODUCT_SKU)
                .build();
        assertThat(cart.getItemCount(), equalTo(1));

        mockMvc.perform(delete("/cart/items/"+PRODUCT_SKU).sessionAttr(CART_KEY, cart)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(cart.getItemCount(), equalTo(0));
    }

    @Test
    public void removeAllItemsFromCart() throws Exception {
        Cart cart = CartBuilder.buildCart()
                .withItem("test01")
                .withItem("test02")
                .build();
        assertThat(cart.getItemCount(), equalTo(2));

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