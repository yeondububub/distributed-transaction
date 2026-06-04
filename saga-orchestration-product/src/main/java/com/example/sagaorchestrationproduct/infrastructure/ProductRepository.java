package com.example.sagaorchestrationproduct.infrastructure;

import com.example.sagaorchestrationproduct.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
