package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.admin.web.models.ProductForm;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductFormValidatorTest {

    private static final String PRODUCT_SKU = "test";

    @Mock
    private CatalogService catalogService;
    @Mock
    private Errors errors;

    @InjectMocks
    private ProductFormValidator validator;

    @Test
    public void validate_UniqueSku_NoErrors() {
        ProductForm form = new ProductForm();
        form.setSku(PRODUCT_SKU);
        when(catalogService.getProductBySku(PRODUCT_SKU)).thenReturn(null);

        validator.validate(form, errors);

        verifyZeroInteractions(errors);
    }

    @Test
    public void validate_FromWithId_NoErrors() {
        ProductForm form = new ProductForm();
        form.setSku(PRODUCT_SKU);
        form.setId(1);
        when(catalogService.getProductBySku(PRODUCT_SKU)).thenReturn(new Product());

        validator.validate(form, errors);

        verifyZeroInteractions(errors);
    }

    @Test
    public void validate_SkuTaken_ValidationError() {
        ProductForm form = new ProductForm();
        form.setSku(PRODUCT_SKU);
        when(catalogService.getProductBySku(PRODUCT_SKU)).thenReturn(new Product());

        validator.validate(form, errors);

        verify(errors, times(1)).rejectValue(anyString(), anyString(), any(), anyString());
    }

    @Test
    public void support_ValidClass_ReturnsTrue() {
        boolean result = validator.supports(ProductForm.class);

        assertTrue(result);
    }

    @Test
    public void support_WrongClass_ReturnsFalse() {
        boolean result = validator.supports(Product.class);

        assertFalse(result);
    }
}