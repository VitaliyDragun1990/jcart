package com.revenat.jcart.admin.web.validators;

import com.revenat.jcart.admin.web.commands.CategoryCommand;
import com.revenat.jcart.core.catalog.CatalogService;
import com.revenat.jcart.core.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.revenat.jcart.admin.web.utils.MessageCodes.ERROR_EXISTS;

@Component
public class CategoryValidator implements Validator {

    @Autowired
    protected CatalogService catalogService;

    @Override
    public boolean supports(Class<?> aClass) {
        return CategoryCommand.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CategoryCommand command = (CategoryCommand) target;
        String name = command.getName();
        Category categoryByName = catalogService.getCategoryByName(name);
        if (categoryByName != null) {
            errors.rejectValue("name", ERROR_EXISTS, new Object[]{name}, "Category " + name + " already exists.");
        }
    }
}
