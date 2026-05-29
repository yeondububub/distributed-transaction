package com.example.tccorder.infrastructure;

import com.example.tccorder.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderItem,Long> {
}
