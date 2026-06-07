package com.example.sagachoreographyproduct.infrastructure;

import com.example.sagachoreographyproduct.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
