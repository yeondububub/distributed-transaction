package com.example.tccproduct.infrastructure;

import com.example.tccproduct.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
