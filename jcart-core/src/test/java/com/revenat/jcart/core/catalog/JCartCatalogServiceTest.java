package com.revenat.jcart.core.catalog;

import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.entities.Product;
import com.revenat.jcart.core.exceptions.JCartException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

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

    private static final String CATEGORY_NAME = "test_category";
    private static final String CATEGORY_DESCRIPTION = "test_category_description";
    private static final int CATEGORY_DISPLAY_ORDER = 1;
    private static final int ID = 1;
    private static final boolean IS_DISABLED = false;
    private static final String PRODUCT_DESCRIPTION = "test_product_description";
    private static final String PRODUCT_SKU = "test_sku";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.ZERO;
    private static final String QUERY = "test";

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private JCartCatalogService catalogService;

    @Test
    public void getAllCategories_ShouldReturnAllCategories() {
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        List<Category> allCategories = catalogService.getAllCategories();

        assertThat(allCategories, hasSize(0));
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void getAllProducts_ShouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        List<Product> allProducts = catalogService.getAllProducts();

        assertThat(allProducts, hasSize(0));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void getCategoryByName_ValidName_ShouldReturnCategory() {
        Category category = new CategoryBuilder()
                .withName(CATEGORY_NAME)
                .build();
        when(categoryRepository.getByName(CATEGORY_NAME)).thenReturn(category);

        Category result = catalogService.getCategoryByName(CATEGORY_NAME);

        assertThat(result, equalTo(category));
        verify(categoryRepository, times(1)).getByName(CATEGORY_NAME);
    }

    @Test
    public void getCategoryByName_InvalidName_ShouldReturnNull() {
        when(categoryRepository.getByName(CATEGORY_NAME)).thenReturn(null);

        Category result = catalogService.getCategoryByName(CATEGORY_NAME);

        assertThat(result, equalTo(null));
        verify(categoryRepository, times(1)).getByName(CATEGORY_NAME);
    }

    @Test
    public void getCategoryById_ValidId_ShouldReturnCategory() {
        Category category = new CategoryBuilder()
                .withId(ID)
                .build();
        when(categoryRepository.findOne(ID)).thenReturn(category);

        Category result= catalogService.getCategoryById(ID);

        assertThat(result, equalTo(category));
        verify(categoryRepository, times(1)).findOne(ID);
    }

    @Test
    public void getCategoryById_InvalidId_ShouldReturnNull() {
        when(categoryRepository.findOne(ID)).thenReturn(null);

        Category result= catalogService.getCategoryById(ID);

        assertThat(result, equalTo(null));
        verify(categoryRepository, times(1)).findOne(ID);
    }

    @Test
    public void createCategory_UniqueNameGiven_CategoryCreated() {
        Category category = new CategoryBuilder()
                .withName(CATEGORY_NAME)
                .build();
        when(categoryRepository.save(isA(Category.class))).then((Answer<Category>) invocationOnMock -> {
            Category categoryToSave = (Category) invocationOnMock.getArguments()[0];
            categoryToSave.setId(ID);
            return categoryToSave;
        });

        Category result = catalogService.createCategory(category);

        assertThat(result.getName(), equalTo(CATEGORY_NAME));
        assertThat(result.getId(), equalTo(ID));
        verify(categoryRepository, times(1)).getByName(CATEGORY_NAME);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test(expected = JCartException.class)
    public void createCategory_ExistedNameGiven_ExceptionThrown() {
        Category category = new CategoryBuilder()
                .withName(CATEGORY_NAME)
                .build();
        when(categoryRepository.getByName(CATEGORY_NAME)).thenReturn(category);

        catalogService.createCategory(category);
    }

    @Test
    public void updateCategory_ValidId_CategoryUpdated() {
        Category existedCategory = new CategoryBuilder()
                .withId(ID)
                .build();
        Category updateHolder = new CategoryBuilder()
                .withId(ID)
                .withDisplayOrder(CATEGORY_DISPLAY_ORDER)
                .withDescription(CATEGORY_DESCRIPTION)
                .setDisabled(IS_DISABLED)
                .build();
        when(categoryRepository.findOne(ID)).thenReturn(existedCategory);
        when(categoryRepository.save(isA(Category.class))).thenAnswer(
                (Answer<Category>) invocationOnMock -> (Category) invocationOnMock.getArguments()[0]);

        Category updatedCategory = catalogService.updateCategory(updateHolder);

        assertThat(updatedCategory.getDescription(), equalTo(CATEGORY_DESCRIPTION));
        assertThat(updatedCategory.getDisplayOrder(), equalTo(CATEGORY_DISPLAY_ORDER));
        assertThat(updatedCategory.isDisabled(), equalTo(IS_DISABLED));
        verify(categoryRepository, times(1)).findOne(ID);
        verify(categoryRepository, times(1)).save(isA(Category.class));
    }

    @Test(expected = JCartException.class)
    public void updateCategory_InvalidId_ExceptionThrown() {
        Category category = new CategoryBuilder()
                .withId(ID)
                .build();
        when(categoryRepository.findOne(ID)).thenReturn(null);

        catalogService.updateCategory(category);
    }

    @Test
    public void getProductById_ValidId_ShouldReturnProduct() {
        Product product = new ProductBuilder()
                .withId(ID)
                .build();
        when(productRepository.findOne(ID)).thenReturn(product);

        Product result = catalogService.getProductById(ID);

        assertThat(result, equalTo(product));
        verify(productRepository, times(1)).findOne(ID);
    }

    @Test
    public void getProductById_InvalidId_ShouldReturnNull() {
        when(productRepository.findOne(ID)).thenReturn(null);

        Product result = catalogService.getProductById(ID);

        assertThat(result, equalTo(null));
        verify(productRepository, times(1)).findOne(ID);
    }

    @Test
    public void getProductBySku_ValidSku_ShouldReturnProduct() {
        Product product = new ProductBuilder()
                .withSku(PRODUCT_SKU)
                .build();
        when(productRepository.findBySku(PRODUCT_SKU)).thenReturn(product);

        Product result = catalogService.getProductBySku(PRODUCT_SKU);

        assertThat(result.getSku(), equalTo(PRODUCT_SKU));
        verify(productRepository, times(1)).findBySku(PRODUCT_SKU);
    }

    @Test
    public void getProductBySku_InvalidSku_ShouldReturnNull() {
        when(productRepository.findBySku(PRODUCT_SKU)).thenReturn(null);

        Product result = catalogService.getProductBySku(PRODUCT_SKU);

        assertThat(result, equalTo(null));
        verify(productRepository, times(1)).findBySku(PRODUCT_SKU);
    }

    @Test
    public void createProduct_UniqueSkuGiven_NewProductCreated() {
        Product newProduct = new ProductBuilder()
                .withSku(PRODUCT_SKU)
                .build();
        when(productRepository.findBySku(PRODUCT_SKU)).thenReturn(null);
        when(productRepository.save(isA(Product.class))).thenAnswer((Answer<Product>) invocationOnMock -> {
            Product productToSave = (Product) invocationOnMock.getArguments()[0];
            productToSave.setId(ID);
            return productToSave;
        });

        Product savedProduct = catalogService.createProduct(newProduct);

        assertThat(savedProduct.getSku(), equalTo(PRODUCT_SKU));
        assertThat(savedProduct.getId(), equalTo(ID));
        verify(productRepository, times(1)).findBySku(PRODUCT_SKU);
        verify(productRepository, times(1)).save(isA(Product.class));
        verifyZeroInteractions(productRepository);
    }

    @Test(expected = JCartException.class)
    public void createProduct_OccupiedSkuGiven_ExceptionThrown() {
        Product newProduct = new ProductBuilder()
                .withSku(PRODUCT_SKU)
                .build();
        when(productRepository.findBySku(PRODUCT_SKU)).thenReturn(new ProductBuilder().build());

        catalogService.createProduct(newProduct);
    }

    @Test
    public void updateProduct_ValidId_ProductUpdated() {
        Product existedProduct = new ProductBuilder()
                .withId(ID)
                .build();
        Category category = new CategoryBuilder().withId(ID).build();
        Product updateHolder = new ProductBuilder()
                .withId(ID)
                .withDescription(PRODUCT_DESCRIPTION)
                .setDisabled(IS_DISABLED)
                .withPrice(PRODUCT_PRICE)
                .withCategory(category)
                .build();
        when(productRepository.findOne(ID)).thenReturn(existedProduct);
        when(categoryRepository.findOne(ID)).thenReturn(category);
        when(productRepository.save(isA(Product.class))).thenAnswer(
                (Answer<Product>) invocationOnMock -> (Product) invocationOnMock.getArguments()[0]);

        Product updatedProduct = catalogService.updateProduct(updateHolder);

        assertThat(updatedProduct.getDescription(), equalTo(PRODUCT_DESCRIPTION));
        assertThat(updatedProduct.isDisabled(), equalTo(IS_DISABLED));
        assertThat(updatedProduct.getPrice(), equalTo(PRODUCT_PRICE));
        assertThat(updatedProduct.getCategory(), equalTo(category));
        verify(productRepository, times(1)).findOne(ID);
        verify(productRepository, times(1)).save(isA(Product.class));
    }

    @Test(expected = JCartException.class)
    public void updateProduct_InvalidIdGiven_ExceptionThrown() {
        Product product = new ProductBuilder()
                .withId(ID)
                .build();
        when(productRepository.findOne(anyInt())).thenReturn(null);

        catalogService.updateProduct(product);
    }

    @Test
    public void searchProduct_QueryGiven_ShouldReturnSearchResult() {
        String completedQuery = "%" + QUERY + "%";
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        when(productRepository.search(anyString())).thenReturn(new ArrayList<>());

        List<Product> products = catalogService.searchProduct(QUERY);

        verify(productRepository, timeout(1)).search(queryCaptor.capture());
        String searchQuery = queryCaptor.getValue();
        assertThat(searchQuery, equalTo(completedQuery));
        assertThat(products, hasSize(0));
    }

}