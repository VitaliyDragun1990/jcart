package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.CategoryCommand;
import com.revenat.jcart.entities.Category;
import org.springframework.core.convert.converter.Converter;

public class CategoryToCategoryCommandConverter implements Converter<Category, CategoryCommand> {
    @Override
    public CategoryCommand convert(Category category) {
        CategoryCommand command = new CategoryCommand();

        command.setId(category.getId());
        command.setName(category.getName());
        command.setDescription(category.getDescription());
        command.setDisplayOrder(category.getDisplayOrder());

        return command;
    }
}
