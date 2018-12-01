package com.revenat.jcart.catalog;

import com.revenat.jcart.entities.Category;
import com.revenat.jcart.entities.Product;
import com.revenat.jcart.exceptions.JCartException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JCartCatalogServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private JCartCatalogService service;

    private Category stubCategory;
    private Product stubProduct;

    @Before
    public void setUpStabs() {
        stubCategory = new Category();
        stubCategory.setName("test");
        stubCategory.setDescription("stub");
        stubCategory.setDisplayOrder(1);
        stubCategory.setDisabled(false);

        stubProduct = new Product();
        stubProduct.setName("test");
        stubProduct.setDescription("stub");
        stubProduct.setDisabled(false);
        stubProduct.setPrice(BigDecimal.ZERO);
        stubProduct.setCategory(stubCategory);
    }

    @Test
    public void getAllCategories() {
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        List<Category> allCategories = service.getAllCategories();

        assertThat(allCategories, hasSize(0));
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void getAllProducts() {
        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        List<Product> allProducts = service.getAllProducts();

        assertThat(allProducts, hasSize(0));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void getCategoryByName() {
        when(categoryRepository.getByName("test")).thenReturn(stubCategory);

        Category category = service.getCategoryByName("test");

        assertThat(category, equalTo(stubCategory));
        verify(categoryRepository, times(1)).getByName(anyString());
    }

    @Test
    public void getCategoryById() {
        when(categoryRepository.findOne(1)).thenReturn(stubCategory);

        Category category = service.getCategoryById(1);

        assertThat(category, equalTo(stubCategory));
        verify(categoryRepository, times(1)).findOne(1);
    }

    @Test
    public void createCategory_Ok() {
        when(categoryRepository.save(Matchers.any(Category.class))).thenReturn(stubCategory);

        Category category = service.createCategory(stubCategory);

        assertThat(category, equalTo(stubCategory));
        verify(categoryRepository, times(1)).getByName(anyString());
        verify(categoryRepository, times(1)).save(stubCategory);
    }

    @Test(expected = JCartException.class)
    public void createCategory_FailNameAlreadyExists() {
        when(categoryRepository.getByName("test")).thenReturn(stubCategory);

        service.createCategory(stubCategory);
    }

    @Test
    public void updateCategory_Ok() {
        Category category = new Category();
        when(categoryRepository.findOne(1)).thenReturn(category);
        when(categoryRepository.save(Matchers.any(Category.class))).thenReturn(category);
        stubCategory.setId(1);

        Category updatedCategory = service.updateCategory(stubCategory);

        assertThat(updatedCategory.getDescription(), equalTo(stubCategory.getDescription()));
        assertThat(updatedCategory.getDisplayOrder(), equalTo(stubCategory.getDisplayOrder()));
        assertThat(updatedCategory.isDisabled(), equalTo(stubCategory.isDisabled()));
        verify(categoryRepository, times(1)).findOne(anyInt());
        verify(categoryRepository, times(1)).save(Matchers.any(Category.class));
    }

    @Test(expected = JCartException.class)
    public void updateCategoryFail_NoSuchCategory() {
        when(categoryRepository.findOne(anyInt())).thenReturn(null);

        service.updateCategory(stubCategory);
    }

    @Test
    public void getProductById() {
        when(productRepository.findOne(anyInt())).thenReturn(stubProduct);

        Product product = service.getProductById(1);

        assertThat(product, equalTo(stubProduct));
        verify(productRepository, times(1)).findOne(1);
    }

    @Test
    public void getProductBySku() {
        when(productRepository.findBySku("test")).thenReturn(stubProduct);

        Product product = service.getProductBySku("test");

        assertThat(product, equalTo(stubProduct));
        verify(productRepository, times(1)).findBySku("test");
    }

    @Test
    public void createProduct_Ok() {
        when(productRepository.save(Matchers.any(Product.class))).thenReturn(stubProduct);

        Product product = service.createProduct(stubProduct);

        assertThat(product, equalTo(stubProduct));
        verify(productRepository, times(1)).findBySku(stubProduct.getSku());
        verify(productRepository, times(1)).save(Matchers.any(Product.class));
    }

    @Test(expected = JCartException.class)
    public void createProduct_FailSkuAlreadyExists() {
        when(productRepository.findBySku(stubProduct.getSku())).thenReturn(stubProduct);

        service.createProduct(stubProduct);
    }

    @Test
    public void updateProduct_Ok() {
        Product product = new Product();
        when(productRepository.findOne(1)).thenReturn(product);
        when(categoryRepository.findOne(1)).thenReturn(stubCategory);
        when(productRepository.save(product)).thenReturn(product);
        stubProduct.setId(1);
        stubCategory.setId(1);

        Product updatedProduct = service.updateProduct(stubProduct);

        assertThat(updatedProduct.getDescription(), equalTo(stubProduct.getDescription()));
        assertThat(updatedProduct.isDisabled(), equalTo(stubProduct.isDisabled()));
        assertThat(updatedProduct.getPrice(), equalTo(stubProduct.getPrice()));
        assertThat(updatedProduct.getCategory(), equalTo(stubProduct.getCategory()));
        verify(productRepository, times(1)).findOne(anyInt());
        verify(productRepository, times(1)).save(Matchers.any(Product.class));
    }

    @Test(expected = JCartException.class)
    public void updateProduct_FailNoSuchProduct() {
        when(productRepository.findOne(anyInt())).thenReturn(null);
        stubProduct.setId(1);

        service.updateProduct(stubProduct);
    }

    @Test
    public void searchProduct() {
        when(productRepository.search(anyString())).thenReturn(new ArrayList<>());

        List<Product> products = service.searchProduct("test");

        assertThat(products, hasSize(0));
    }
}