package com.revenat.jcart.catalog;

import com.revenat.jcart.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category getByName(String name);
}
