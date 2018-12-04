package com.revenat.jcart.core.catalog;

import com.revenat.jcart.core.JCartCoreApplication;
import com.revenat.jcart.core.entities.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = JCartCoreApplication.class)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void getCategoryByName_Ok() {
        Category flowers = categoryRepository.getByName("Flowers");

        assertThat(flowers.getName(), equalTo("Flowers"));
    }

    @Test
    public void getCategoryByName_FailUnknownName() {
        Category unknown = categoryRepository.getByName("test");

        assertNull(unknown);
    }
}