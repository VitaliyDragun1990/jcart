package com.revenat.jcart.admin.web.converters;

import com.revenat.jcart.admin.web.commands.CategoryCommand;
import com.revenat.jcart.core.entities.Category;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CategoryToCategoryCommandConverterTest {
    private static final Integer ID = 1;
    private static final String NAME = "test_category";
    private static final String DESCRIPTION = "test";
    private static final Integer DISPLAY_ORDER = 1;

    private CategoryToCategoryCommandConverter converter = new CategoryToCategoryCommandConverter();

    @Test
    public void convert_CategoryGiven_CategoryCommandReturned() {
        Category category = createCategory(ID, NAME, DESCRIPTION, DISPLAY_ORDER);

        CategoryCommand command = converter.convert(category);

        assertThat(command.getId(), equalTo(ID));
        assertThat(command.getName(), equalTo(NAME));
        assertThat(command.getDescription(), equalTo(DESCRIPTION));
        assertThat(command.getDisplayOrder(), equalTo(DISPLAY_ORDER));
    }

    private Category createCategory(Integer id, String name, String description, Integer displayOrder) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        category.setDisplayOrder(displayOrder);

        return category;
    }
}