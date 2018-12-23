package com.revenat.jcart.admin.web.controllers.builders;

import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.entities.Product;

import java.math.BigDecimal;

public class ProductBuilder {
    private Product product;

    public ProductBuilder() {
        this.product = new Product();
    }

    public Product build() {
        return this.product;
    }

    public static ProductBuilder getBuilder() {
        return new ProductBuilder();
    }

    public ProductBuilder withName(String name) {
        this.product.setName(name);
        return this;
    }

    public ProductBuilder withDescription(String description) {
        this.product.setDescription(description);
        return this;
    }

    public ProductBuilder withSku(String sku) {
        this.product.setSku(sku);
        return this;
    }

    public ProductBuilder withPrice(BigDecimal price) {
        this.product.setPrice(price);
        return this;
    }

    public ProductBuilder setDisabled(boolean isDisabled) {
        this.product.setDisabled(isDisabled);
        return this;
    }

    public ProductBuilder withCategory(Category category) {
        this.product.setCategory(category);
        return this;
    }

    public ProductBuilder withId(Integer id) {
        this.product.setId(id);
        return this;
    }
}
