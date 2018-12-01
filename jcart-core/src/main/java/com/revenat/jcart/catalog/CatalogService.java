package com.revenat.jcart.catalog;

import com.revenat.jcart.entities.Category;
import com.revenat.jcart.entities.Product;

import java.util.List;

public interface CatalogService {

    List<Category> getAllCategories();

    List<Product> getAllProducts();

    Category getCategoryByName(String name);

    Category getCategoryById(Integer id);

    Category createCategory(Category category);

    Category updateCategory(Category category);

    Product getProductById(Integer id);

    Product getProductBySku(String sku);

    Product createProduct(Product product);

    Product updateProduct(Product product);

    List<Product> searchProduct(String query);
}
