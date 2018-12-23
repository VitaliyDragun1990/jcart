package com.revenat.jcart.core.catalog;

import com.revenat.jcart.core.JCartCoreApplication;
import com.revenat.jcart.core.entities.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = JCartCoreApplication.class)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void fundByName_ValidNameGiven_ProductReturned() {
        String productName = "Quilling Toy 1";

        Product product = productRepository.findByName(productName);

        assertThat("Product name should be equal to those by which this product was found.",
                product.getName(), equalTo(productName));
    }

    @Test
    public void findByName_UnknownNameGiven_NullReturned() {
        String unknownName = "test";

        Product product = productRepository.findByName(unknownName);

        assertNull("Search by wrong name should return null.", product);
    }

    @Test
    public void findBySku_ValidSkuGiven_ProductReturned() {
        String productSku = "P1001";

        Product product = productRepository.findBySku(productSku);

        assertThat("Product SKU should be equal to those by which this product was found.",
                product.getSku(), equalTo(productSku));
    }

    @Test
    public void findBySku_UnknownSkuGiven_NullReturned() {
        String invalidSku = "test";

        Product product = productRepository.findBySku(invalidSku);

        assertNull("Search by wrong SKU should return null.", product);
    }

    @Test
    public void search_ValidQueryGiven_ProductListReturned() {
        String query = "%P100%";

        List<Product> products = productRepository.search(query);

        assertThat("Result list should contain all products compatible with given query.",
                products, hasSize(5));
    }

    @Test
    public void search_NoResultQueryGiven_EmptyListReturned() {
        String query = "%test%";

        List<Product> products = productRepository.search(query);

        assertThat("Result list should be empty if no products compatible with given query.",
                products, hasSize(0));
    }
}