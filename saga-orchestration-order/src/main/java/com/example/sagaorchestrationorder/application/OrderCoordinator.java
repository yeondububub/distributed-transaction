package com.example.sagaorchestrationorder.application;

import com.example.sagaorchestrationorder.application.dto.OrderDto;
import com.example.sagaorchestrationorder.application.dto.PlaceOrderCommand;
import com.example.sagaorchestrationorder.infrastructure.point.PointApiClient;
import com.example.sagaorchestrationorder.infrastructure.point.PointUseApiRequest;
import com.example.sagaorchestrationorder.infrastructure.point.PointUseCancelApiRequest;
import com.example.sagaorchestrationorder.infrastructure.product.*;
import org.springframework.stereotype.Component;

@Component
public class OrderCoordinator {

    private final OrderService orderService;
    private final ProductApiClient productApiClient;
    private final PointApiClient pointApiClient;

    public OrderCoordinator(OrderService orderService, ProductApiClient productApiClient, PointApiClient pointApiClient) {
        this.orderService = orderService;
        this.productApiClient = productApiClient;
        this.pointApiClient = pointApiClient;
    }

    public void placeOrder(PlaceOrderCommand command) {
        orderService.request(command.orderId());
        OrderDto orderDto = orderService.getOrder(command.orderId());

        try {
            ProductBuyApiRequest productBuyApiRequest = new ProductBuyApiRequest(
                    command.orderId().toString(),
                    orderDto.orderItems().stream()
                            .map(item -> new ProductBuyApiRequest.ProductInfo(item.productId(), item.quantity()))
                            .toList()
            );

            ProductBuyApiResponse buyApiResponse = productApiClient.buy(productBuyApiRequest);

            PointUseApiRequest pointUseApiRequest = new PointUseApiRequest(
                    command.orderId().toString(),
                    1L,
                    buyApiResponse.totalPrice()
            );

            pointApiClient.use(pointUseApiRequest);
            orderService.complete(command.orderId());

        } catch (Exception e) {
            ProductBuyCancelApiRequest productBuyCancelApiRequest = new ProductBuyCancelApiRequest(command.orderId().toString());

            ProductBuyCancelApiResponse productBuyCancelApiResponse = productApiClient.cancel(productBuyCancelApiRequest);

            if (productBuyCancelApiResponse.totalPrice() > 0) {
                PointUseCancelApiRequest pointUseCancelApiRequest = new PointUseCancelApiRequest(command.orderId().toString());
                pointApiClient.cancel(pointUseCancelApiRequest);
            }

            orderService.fail(command.orderId());
        }
    }
}
