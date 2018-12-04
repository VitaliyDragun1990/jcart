package com.revenat.jcart.core.catalog;

import com.revenat.jcart.core.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByName(String name);

    Product findBySku(String sku);

    @Query("SELECT p FROM Product p WHERE p.name LIKE ?1 OR p.sku LIKE ?1 OR p.description LIKE ?1")
    List<Product> search(String query);
}
