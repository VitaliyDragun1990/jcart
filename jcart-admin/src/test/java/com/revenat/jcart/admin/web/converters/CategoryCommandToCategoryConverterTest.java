package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.CategoryCommand;
import com.revenat.jcart.core.entities.Category;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CategoryCommandToCategoryConverterTest {

    private CategoryCommandToCategoryConverter converter = new CategoryCommandToCategoryConverter();

    @Test
    public void testConvertCategoryCommandToCategory() {
        Integer id = 1;
        String name = "test_category";
        String description = "test";
        Integer displayOrder = 1;
        CategoryCommand command = new CategoryCommand();
        command.setId(id);
        command.setName(name);
        command.setDescription(description);
        command.setDisplayOrder(displayOrder);

        Category category = converter.convert(command);

        assertThat(category.getId(), equalTo(id));
        assertThat(category.getName(), equalTo(name));
        assertThat(category.getDescription(), equalTo(description));
        assertThat(category.getDisplayOrder(), equalTo(displayOrder));
    }
}