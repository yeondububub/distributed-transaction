package com.example.tccorder.infrastructure;

import com.example.tccorder.domain.Order;
import com.example.tccorder.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
