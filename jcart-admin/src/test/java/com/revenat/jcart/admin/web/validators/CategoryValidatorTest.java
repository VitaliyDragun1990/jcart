package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.admin.web.commands.CategoryCommand;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Category;
import com.revenat.jcart.core.entities.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoryValidatorTest {

    private static final String CATEGORY_NAME = "DUMMY_CATEGORY";

    @Mock
    private CatalogService catalogService;
    @Mock
    private Errors errors;

    @InjectMocks
    private CategoryValidator validator;

    @Test
    public void testValidate_Ok() {
        CategoryCommand command = new CategoryCommand();
        command.setName(CATEGORY_NAME);

        validator.validate(command, errors);

        verifyZeroInteractions(errors);
    }

    @Test
    public void testValidate_Error() {
        CategoryCommand command = new CategoryCommand();
        command.setName(CATEGORY_NAME);
        when(catalogService.getCategoryByName(CATEGORY_NAME)).thenReturn(new Category());

        validator.validate(command, errors);

        verify(errors, times(1)).rejectValue(anyString(), anyString(), any(), anyString());
    }

    @Test
    public void testSupport_Ok() {
        boolean result = validator.supports(CategoryCommand.class);

        assertTrue(result);
    }

    @Test
    public void testSupport_Fail() {
        boolean result = validator.supports(Product.class);

        assertFalse(result);
    }
}