package com.example.sagaorchestrationpoint.infrastructure;

import com.example.sagaorchestrationpoint.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
    Point findByUserId(Long userId);
}