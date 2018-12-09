package com.revenat.jcart.site.web.models;

import com.revenat.jcart.core.entities.Address;
import com.revenat.jcart.core.entities.Customer;
import com.revenat.jcart.core.entities.Payment;
import com.revenat.jcart.core.entities.Product;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class CartTest {

    private Cart cart;

    @Before
    public void setUp() {
        cart = new Cart();
    }

    @Test
    public void createsEmptyCart() {
        cart = new Cart();

        assertThat(cart.getItemCount(), equalTo(0));
        assertThat(cart.getItems(), hasSize(0));
        assertThat(cart.getTotalAmount(), equalTo(BigDecimal.ZERO));
        assertThat(cart.getCustomer(), equalTo(new Customer()));
        assertThat(cart.getPayment(), equalTo(new Payment()));
        assertThat(cart.getDeliveryAddress(), equalTo(new Address()));
    }

    @Test
    public void addUniqueProductToCart_IncreaseLineItemsSize() {
        assertThat(cart.getItems(), hasSize(0));

        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test02", BigDecimal.ONE));

        assertThat(cart.getItems(), hasSize(2));
    }

    @Test
    public void addUniqueProductToCart_IncreaseItemCount() {
        assertThat(cart.getItemCount(), equalTo(0));

        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test02", BigDecimal.ONE));

        assertThat(cart.getItemCount(), equalTo(2));
    }

    @Test
    public void addSameProductSeveralTimes_IncreaseItemCountNotLineItemSize() {
        assertThat(cart.getItems(), hasSize(0));
        assertThat(cart.getItemCount(), equalTo(0));

        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test01", BigDecimal.ONE));

        assertThat(cart.getItems(), hasSize(1));
        assertThat(cart.getItemCount(), equalTo(3));
    }

    @Test
    public void updateItemQuantityIfProductInCart() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(2));

        cart.updateItemQuantity(product, 1);

        assertThat(cart.getItemCount(), equalTo(1));
    }

    @Test
    public void notUpdateItemQuantityIfProductNotInCart() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(2));

        cart.updateItemQuantity(buildProduct("test02", BigDecimal.ONE), 1);

        assertThat(cart.getItemCount(), equalTo(2));
    }

    @Test
    public void removesItemFromCartByProduct_IfExistInCart() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(1));

        cart.removeItem(product);

        assertThat(cart.getItemCount(), equalTo(0));
    }

    @Test
    public void removesItemFromCartBySku_IfExistInCart() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(1));

        cart.removeItem("test01");

        assertThat(cart.getItemCount(), equalTo(0));
    }

    @Test
    public void DontChangeItemCount_IfRemoveProductNotInCart() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(1));

        cart.removeItem(buildProduct("test02", BigDecimal.ONE));

        assertThat(cart.getItemCount(), equalTo(1));
    }

    @Test
    public void removeAllItemsFromCart() {
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        assertThat(cart.getItemCount(), equalTo(3));

        cart.clearItems();

        assertThat(cart.getItemCount(), equalTo(0));
    }

    @Test
    public void returnsZeroAmountIfCartEmpty() {
        BigDecimal totalAmount = cart.getTotalAmount();

        assertThat(totalAmount, equalTo(BigDecimal.ZERO));
    }

    @Test
    public void returnsTotalAmountOfAllItemInCart() {
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test02", BigDecimal.TEN));

        BigDecimal totalAmount = cart.getTotalAmount();

        BigDecimal expectedAmount = new BigDecimal("12");
        assertThat(totalAmount, equalTo(expectedAmount));
    }

    private Product buildProduct(String sku, BigDecimal price) {
        Product product = new Product();
        product.setSku(sku);
        product.setPrice(price);
        return product;
    }
}