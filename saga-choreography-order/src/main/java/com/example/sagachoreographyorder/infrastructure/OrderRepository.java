package com.example.sagachoreographyorder.infrastructure;

import com.example.sagachoreographyorder.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
