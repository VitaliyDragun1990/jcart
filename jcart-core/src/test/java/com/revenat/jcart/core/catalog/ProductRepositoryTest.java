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
    public void getProductByName_Ok() {
        String productName = "Quilling Toy 1";

        Product product = productRepository.findByName(productName);

        assertThat(product.getName(), equalTo(productName));
    }

    @Test
    public void getProductByName_FailUnknownName() {
        String unknownName = "test";

        Product product = productRepository.findByName(unknownName);

        assertNull(product);
    }

    @Test
    public void getProductBySku_Ok() {
        String productSku = "P1001";

        Product product = productRepository.findBySku(productSku);

        assertThat(product.getSku(), equalTo(productSku));
    }

    @Test
    public void getProductBySku_FailInvalidSku() {
        String invalidSku = "test";

        Product product = productRepository.findBySku(invalidSku);

        assertNull(product);
    }

    @Test
    public void searchProductByQuery_Ok() {
        String query = "%P100%";

        List<Product> products = productRepository.search(query);

        assertThat(products, hasSize(5));
    }

    @Test
    public void searchProductByQuery_FailNoResult() {
        String query = "%test%";

        List<Product> products = productRepository.search(query);

        assertThat(products, hasSize(0));
    }
}