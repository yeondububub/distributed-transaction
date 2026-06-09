package com.example.sagachoreographyorder.consumer;

import com.example.sagachoreographyorder.application.OrderService;
import com.example.sagachoreographyorder.consumer.dto.QuantityDecreasedFailEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class QuantityDecreasedFailConsumer {

    private final OrderService orderService;

    public QuantityDecreasedFailConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
            topics = "quantity-decreased-fail",
            groupId = "quantity-decreased-fail-consumer",
            properties = {
                    "spring.json.value.default.type=com.example.sagachoreographyorder.consumer.dto.QuantityDecreasedFailEvent"
            }
    )
    public void handle(QuantityDecreasedFailEvent event) {
        orderService.fail(event.orderId());
    }
}
