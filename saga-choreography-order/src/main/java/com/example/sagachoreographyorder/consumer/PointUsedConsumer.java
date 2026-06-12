package com.example.sagachoreographyorder.consumer;

import com.example.sagachoreographyorder.application.OrderService;
import com.example.sagachoreographyorder.consumer.dto.PointUsedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PointUsedConsumer {
    private final OrderService orderService;

    public PointUsedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
            topics = "point-used",
            groupId = "point-used-consumer",
            properties = {
                    "spring.json.value.default.type=com.example.sagachoreographyorder.consumer.dto.PointUsedEvent"
            }
    )
    public void handle(PointUsedEvent event) {
        orderService.complete(event.orderId());
    }
}
