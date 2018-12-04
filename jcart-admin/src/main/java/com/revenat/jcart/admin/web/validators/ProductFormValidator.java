package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.admin.web.models.ProductForm;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.revenat.jcart.admin.web.utils.MessageCodes.ERROR_EXISTS;

@Component
public class ProductFormValidator implements Validator {

    @Autowired
    protected CatalogService catalogService;

    @Override
    public boolean supports(Class<?> aClass) {
        return ProductForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductForm form = (ProductForm) target;
        String sku = form.getSku();
        Product product = catalogService.getProductBySku(sku);
        if (product != null && newProductForm(form)) {
            errors.rejectValue("sku", ERROR_EXISTS, new Object[]{sku}, "Product SKU " + sku + " already exists.");
        }
    }

    private boolean newProductForm(ProductForm form) {
        return form.getId() == null;
    }
}
