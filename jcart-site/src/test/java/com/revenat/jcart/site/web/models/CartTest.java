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
    public void defaultConstructor_CreatesEmptyCart() {
        cart = new Cart();

        assertThat(cart.getItemCount(), equalTo(0));
        assertThat(cart.getItems(), hasSize(0));
        assertThat(cart.getTotalAmount(), equalTo(BigDecimal.ZERO));
        assertThat(cart.getCustomer(), equalTo(new Customer()));
        assertThat(cart.getPayment(), equalTo(new Payment()));
        assertThat(cart.getDeliveryAddress(), equalTo(new Address()));
    }

    @Test
    public void addItem_DistinctProductGiven_IncreasesLineItemsSize() {
        assertThat(cart.getItems(), hasSize(0));

        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test02", BigDecimal.ONE));

        assertThat(cart.getItems(), hasSize(2));
    }

    @Test
    public void addItem_DistinctProductGiven_IncreasesItemCount() {
        assertThat(cart.getItemCount(), equalTo(0));

        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test02", BigDecimal.ONE));

        assertThat(cart.getItemCount(), equalTo(2));
    }

    @Test
    public void addItem_SameProductSeveralTimes_IncreasesItemCountNotLineItemsSize() {
        assertThat(cart.getItems(), hasSize(0));
        assertThat(cart.getItemCount(), equalTo(0));

        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test01", BigDecimal.ONE));

        assertThat(cart.getItems(), hasSize(1));
        assertThat(cart.getItemCount(), equalTo(3));
    }

    @Test
    public void updateItemQuantity_ProductAsInCartGiven_ChangesItemCount() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(2));

        cart.updateItemQuantity(product, 1);

        assertThat(cart.getItemCount(), equalTo(1));
    }

    @Test
    public void updateItemQuantity_ProductNotInCartGiven_ShouldNotChangeItemCount() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(2));

        cart.updateItemQuantity(buildProduct("test02", BigDecimal.ONE), 1);

        assertThat(cart.getItemCount(), equalTo(2));
    }

    @Test
    public void removeItem_ProductInCartGiven_RemovesSuchProductFromCart() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(1));

        cart.removeItem(product);

        assertThat(cart.getItemCount(), equalTo(0));
    }

    @Test
    public void removeItem_SkuFromInProductInCartGiven_RemovesSuchProductFromCart() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(1));

        cart.removeItem("test01");

        assertThat(cart.getItemCount(), equalTo(0));
    }

    @Test
    public void removeItem_productNotInCartGiven_DoesNotChangeCart() {
        Product product = buildProduct("test01", BigDecimal.ONE);
        cart.addItem(product);
        assertThat(cart.getItemCount(), equalTo(1));

        cart.removeItem(buildProduct("test02", BigDecimal.ONE));

        assertThat(cart.getItemCount(), equalTo(1));
    }

    @Test
    public void clearItems_RemovesAllProductsFromCart() {
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        cart.addItem(buildProduct("test01", BigDecimal.ONE));
        assertThat(cart.getItemCount(), equalTo(3));

        cart.clearItems();

        assertThat(cart.getItemCount(), equalTo(0));
    }

    @Test
    public void getTotalAmount_CartEmpty_ReturnsZero() {
        BigDecimal totalAmount = cart.getTotalAmount();

        assertThat(totalAmount, equalTo(BigDecimal.ZERO));
    }

    @Test
    public void getTotalAmount_CartWithItems_ReturnsSumOfItemsAmounts() {
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