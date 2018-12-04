package com.revenat.jcart.admin.web.models;

import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.entities.Product;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductForm {

    private Integer id;
    @NotEmpty
    private String sku;
    @NotEmpty
    private String name;
    private String description;
    @NotNull
    @DecimalMin("0.1")
    private BigDecimal price = new BigDecimal("0.0");
    private String imageUrl;
    private MultipartFile image;
    private boolean disabled;
    @NotNull
    private Integer categoryId;

    public Product toProduct() {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setDisabled(disabled);
        product.setPrice(price);
        product.setSku(sku);

        Category category = new Category();
        category.setId(categoryId);
        product.setCategory(category);

        return product;
    }

    public static ProductForm fromProduct(Product product) {
        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setDescription(product.getDescription());
        form.setDisabled(product.isDisabled());
        form.setPrice(product.getPrice());
        form.setSku(product.getSku());
        form.setCategoryId(product.getCategory().getId());

        return form;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
