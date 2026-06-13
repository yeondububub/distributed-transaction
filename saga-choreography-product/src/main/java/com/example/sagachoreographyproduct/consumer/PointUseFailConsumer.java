package com.example.sagachoreographyproduct.consumer;

import com.example.sagachoreographyproduct.application.ProductService;
import com.example.sagachoreographyproduct.application.dto.ProductBuyCancelCommand;
import com.example.sagachoreographyproduct.consumer.dto.PointUseFailEvent;
import com.example.sagachoreographyproduct.infrastructure.kafka.QuantityDecreasedFailProducer;
import com.example.sagachoreographyproduct.infrastructure.kafka.dto.QuantityDecreasedFailEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PointUseFailConsumer {

    private final ProductService productService;
    private final QuantityDecreasedFailProducer  quantityDecreasedFailProducer;

    public PointUseFailConsumer(ProductService productService, QuantityDecreasedFailProducer quantityDecreasedFailProducer) {
        this.productService = productService;
        this.quantityDecreasedFailProducer = quantityDecreasedFailProducer;
    }

    @KafkaListener(
            topics = "point-use-fail",
            groupId = "point-use-fail-comsumer",
            properties = {
                    "spring.json.value.default.type=com.example.sagachoreographyproduct.consumer.dto.PointUseFailEvent"
            }
    )
    public void handle(PointUseFailEvent event) {
        productService.cancel(new ProductBuyCancelCommand(event.orderId().toString()));
        quantityDecreasedFailProducer.send(new QuantityDecreasedFailEvent(event.orderId()));
    }
}
