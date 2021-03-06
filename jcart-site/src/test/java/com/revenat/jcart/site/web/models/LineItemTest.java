package com.revenat.jcart.site.web.models;

import com.revenat.jcart.core.entities.Product;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class LineItemTest {

    private static final Product PRODUCT = new Product();

    private LineItem lineItem;

    @Before
    public void setUp() {
        lineItem = new LineItem();
    }

    @Test
    public void getSubtotal_PositiveQuantityGiven_ReturnsPositiveSubtotal() {
        Integer quantity = 10;
        PRODUCT.setPrice(BigDecimal.TEN);
        lineItem.setProduct(PRODUCT);
        lineItem.setQuantity(quantity);

        BigDecimal actualSubtotal = lineItem.getSubTotal();

        BigDecimal expectedSubtotal = BigDecimal.TEN.multiply(BigDecimal.TEN);
        assertThat(actualSubtotal, equalTo(expectedSubtotal));
    }

    @Test
    public void getSubtotal_ZeroQuantityGiven_ReturnsZeroSubtotal() {
        Integer quantity = 0;
        PRODUCT.setPrice(BigDecimal.TEN);
        lineItem.setQuantity(quantity);
        lineItem.setProduct(PRODUCT);

        BigDecimal actualSubTotal = lineItem.getSubTotal();

        BigDecimal expectedSubtotal = BigDecimal.ZERO;
        assertThat(actualSubTotal, equalTo(expectedSubtotal));
    }

    @Test
    public void contains_HasProductWithSkuEqualToGiven_ReturnsTrue() {
        PRODUCT.setSku("test");
        lineItem.setProduct(PRODUCT);

        boolean result = lineItem.contains(PRODUCT);

        assertTrue(result);
    }

    @Test
    public void contains_HasProductWithDifferentSkuToGiven_ReturnsFalse() {
        PRODUCT.setSku("test");
        lineItem.setProduct(PRODUCT);

        Product dummy = new Product();
        dummy.setSku("dummy");
        boolean result = lineItem.contains(dummy);

        assertFalse(result);
    }
}