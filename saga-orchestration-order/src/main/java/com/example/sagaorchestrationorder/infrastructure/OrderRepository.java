package com.example.sagaorchestrationorder.infrastructure;

import com.example.sagaorchestrationorder.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
