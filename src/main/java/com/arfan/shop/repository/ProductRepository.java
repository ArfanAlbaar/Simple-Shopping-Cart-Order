package com.arfan.shop.repository;

import com.arfan.shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryNameContaining(String category);

    List<Product> findByBrandContaining(String brand);

    List<Product> findByCategoryNameAndBrand(String category, String brand);

    List<Product> findByNameContaining(String name);

    List<Product> findByBrandAndName(String brand, String name);

    Long countByBrandAndName(String brand, String name);

    boolean existsByNameAndBrand(String name, String brand);
}
