package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.CategoryCommand;
import com.revenat.jcart.core.entities.Category;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CategoryCommandToCategoryConverterTest {
    private static final Integer ID = 1;
    private static final String NAME = "test_category";
    private static final String DESCRIPTION = "test";
    private static final Integer DISPLAY_ORDER = 1;

    private CategoryCommandToCategoryConverter converter = new CategoryCommandToCategoryConverter();

    @Test
    public void convert_CategoryCommandGiven_CategoryReturned() {
        CategoryCommand command = createCategoryCommand(ID, NAME, DESCRIPTION, DISPLAY_ORDER);

        Category category = converter.convert(command);

        assertThat(category.getId(), equalTo(ID));
        assertThat(category.getName(), equalTo(NAME));
        assertThat(category.getDescription(), equalTo(DESCRIPTION));
        assertThat(category.getDisplayOrder(), equalTo(DISPLAY_ORDER));
    }

    private CategoryCommand createCategoryCommand(Integer id, String name, String description, Integer displayOrder) {
        CategoryCommand command = new CategoryCommand();
        command.setId(id);
        command.setName(name);
        command.setDescription(description);
        command.setDisplayOrder(displayOrder);
        return command;
    }
}