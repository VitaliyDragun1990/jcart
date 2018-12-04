package com.revenat.jcart.core.catalog;

import com.revenat.jcart.core.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category getByName(String name);
}
