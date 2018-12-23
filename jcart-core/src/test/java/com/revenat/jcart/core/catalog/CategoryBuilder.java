package com.revenat.jcart.core.catalog;

import com.revenat.jcart.core.entities.Category;

public class CategoryBuilder {
    private Category category;

    public CategoryBuilder() {
        this.category = new Category();
    }

    public Category build() {
        return this.category;
    }

    public CategoryBuilder withName(String name) {
        this.category.setName(name);
        return this;
    }

    public CategoryBuilder withDescription(String description) {
        this.category.setDescription(description);
        return this;
    }

    public CategoryBuilder withDisplayOrder(Integer displayOrder) {
        this.category.setDisplayOrder(displayOrder);
        return this;
    }

    public CategoryBuilder setDisabled(boolean isDisabled) {
        this.category.setDisabled(isDisabled);
        return this;
    }

    public CategoryBuilder withId(Integer id) {
        this.category.setId(id);
        return this;
    }
}
