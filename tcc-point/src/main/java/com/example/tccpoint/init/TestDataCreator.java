package com.example.tccpoint.init;

import com.example.tccpoint.domain.Point;
import com.example.tccpoint.infrastructure.PointRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TestDataCreator {
    private final PointRepository pointRepository;

    public TestDataCreator(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @PostConstruct
    public void createTestData() {
        pointRepository.save(new Point(1L, 10000L));
    }
}
