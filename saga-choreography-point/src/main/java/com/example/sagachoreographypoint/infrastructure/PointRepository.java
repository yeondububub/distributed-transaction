package com.example.sagachoreographypoint.infrastructure;

import com.example.sagachoreographypoint.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
    Point findByUserId(Long userId);
}