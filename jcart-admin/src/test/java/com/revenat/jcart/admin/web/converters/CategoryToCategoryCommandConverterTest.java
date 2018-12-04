package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.CategoryCommand;
import com.revenat.jcart.core.entities.Category;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CategoryToCategoryCommandConverterTest {

    private CategoryToCategoryCommandConverter converter = new CategoryToCategoryCommandConverter();

    @Test
    public void testConvertCategoryToCategoryCommand() {
        Integer id = 1;
        String name = "test_category";
        String description = "test";
        Integer displayOrder = 1;
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        category.setDisplayOrder(displayOrder);

        CategoryCommand command = converter.convert(category);

        assertThat(command.getId(), equalTo(id));
        assertThat(command.getName(), equalTo(name));
        assertThat(command.getDescription(), equalTo(description));
        assertThat(command.getDisplayOrder(), equalTo(displayOrder));
    }
}