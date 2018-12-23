package com.revenat.jcart.core.catalog;

import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.entities.Product;
import com.revenat.jcart.core.exceptions.JCartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Profile("!test")
public class JCartCatalogService implements CatalogService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.getByName(name);
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryRepository.findOne(id);
    }

    @Override
    public Category createCategory(Category category) {
        Category persistedCategory = getCategoryByName(category.getName());
        if (persistedCategory != null) {
            throw new JCartException("Category " + category.getName() + " already exists");
        }

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        Category persistedCategory = getCategoryById(category.getId());
        if (persistedCategory == null) {
            throw new JCartException("Category " + category.getId() + " doesn't exist");
        }

        persistedCategory.setDescription(category.getDescription());
        persistedCategory.setDisplayOrder(category.getDisplayOrder());
        persistedCategory.setDisabled(category.isDisabled());

        return categoryRepository.save(persistedCategory);
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findOne(id);
    }

    @Override
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    public Product createProduct(Product product) {
        Product persistedProduct = getProductBySku(product.getSku());
        if (persistedProduct != null) {
            throw new JCartException("Product SKU " + product.getSku() + " already exists");
        }

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        Product persistedProduct = getProductById(product.getId());
        if (persistedProduct == null) {
            throw new JCartException("Product " + product.getId() + " doesn't exist");
        }

        persistedProduct.setDescription(product.getDescription());
        persistedProduct.setDisabled(product.isDisabled());
        persistedProduct.setPrice(product.getPrice());
        persistedProduct.setCategory(getCategoryById(product.getCategory().getId()));

        return productRepository.save(persistedProduct);
    }

    @Override
    public List<Product> searchProduct(String query) {
        return productRepository.search("%" + query + "%");
    }
}
