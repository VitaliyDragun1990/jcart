package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.CategoryCommand;
import com.revenat.jcart.core.entities.Category;
import org.springframework.core.convert.converter.Converter;

public class CategoryCommandToCategoryConverter implements Converter<CategoryCommand, Category> {

    @Override
    public Category convert(CategoryCommand command) {
        Category category = new Category();

        category.setId(command.getId());
        category.setName(command.getName());
        category.setDescription(command.getDescription());
        category.setDisplayOrder(command.getDisplayOrder());

        return category;
    }
}
