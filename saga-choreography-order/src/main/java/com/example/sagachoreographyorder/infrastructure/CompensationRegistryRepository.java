package com.example.sagachoreographyorder.infrastructure;

import com.example.sagachoreographyorder.domain.CompensationRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompensationRegistryRepository extends JpaRepository<CompensationRegistry, Long> {
}
