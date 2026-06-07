package com.example.sagaorchestrationorder.infrastructure;

import com.example.sagaorchestrationorder.domain.CompensationRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompensationRegistryRepository extends JpaRepository<CompensationRegistry, Long> {
}
