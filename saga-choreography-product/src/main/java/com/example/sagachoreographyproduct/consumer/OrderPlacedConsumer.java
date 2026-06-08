package com.example.sagachoreographyproduct.consumer;

import com.example.sagachoreographyproduct.application.ProductService;
import com.example.sagachoreographyproduct.application.dto.ProductBuyCancelCommand;
import com.example.sagachoreographyproduct.application.dto.ProductBuyCommand;
import com.example.sagachoreographyproduct.application.dto.ProductBuyResult;
import com.example.sagachoreographyproduct.consumer.dto.OrderPlacedEvent;
import com.example.sagachoreographyproduct.infrastructure.kafka.QuantityDecreasedFailProducer;
import com.example.sagachoreographyproduct.infrastructure.kafka.QuantityDecreasedProducer;
import com.example.sagachoreographyproduct.infrastructure.kafka.dto.QuantityDecreasedEvent;
import com.example.sagachoreographyproduct.infrastructure.kafka.dto.QuantityDecreasedFailEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPlacedConsumer {

    private final ProductService productService;
    private final QuantityDecreasedProducer quantityDecreasedProducer;
    private final QuantityDecreasedFailProducer quantityDecreasedFailProducer;

    public OrderPlacedConsumer(ProductService productService, QuantityDecreasedProducer quantityDecreasedProducer, QuantityDecreasedFailProducer quantityDecreasedFailProducer) {
        this.productService = productService;
        this.quantityDecreasedProducer = quantityDecreasedProducer;
        this.quantityDecreasedFailProducer = quantityDecreasedFailProducer;
    }

    @KafkaListener(
            topics = "order-placed",
            groupId = "order-placed-consumer",
            properties = {
                    "spring.json.value.default.type=com.example.sagachoreographyproduct.consumer.dto.OrderPlacedEvent"
            }
    )
    public void handle(OrderPlacedEvent event) {
        String requestId = event.orderId().toString();

        try {
            ProductBuyResult result = productService.buy(
                    new ProductBuyCommand(
                            requestId,
                            event.productInfos()
                                    .stream()
                                    .map(info -> new ProductBuyCommand.ProductInfo(info.productId(), info.quantity()))
                                    .toList()
                    )
            );

            quantityDecreasedProducer.send(
                    new QuantityDecreasedEvent(
                            event.orderId(),
                            result.totalPrice()
                    )
            );
        } catch (Exception e) {
            productService.cancel(
                    new ProductBuyCancelCommand(requestId)
            );

            quantityDecreasedFailProducer.send(
                    new QuantityDecreasedFailEvent(event.orderId())
            );
        }
    }


}
