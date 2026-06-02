package com.example.tccorder.application;

import com.example.tccorder.application.dto.OrderDto;
import com.example.tccorder.application.dto.PlaceOrderCommand;
import com.example.tccorder.infrastructure.point.PointApiClient;
import com.example.tccorder.infrastructure.point.dto.PointReserveApiRequest;
import com.example.tccorder.infrastructure.point.dto.PointReserveCancelApiRequest;
import com.example.tccorder.infrastructure.point.dto.PointReserveConfirmApiRequest;
import com.example.tccorder.infrastructure.product.ProductApiClient;
import com.example.tccorder.infrastructure.product.dto.ProductReserveApiRequest;
import com.example.tccorder.infrastructure.product.dto.ProductReserveApiResponse;
import com.example.tccorder.infrastructure.product.dto.ProductReserveCancelApiRequest;
import com.example.tccorder.infrastructure.product.dto.ProductReserveConfirmApiRequest;
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
        reserve(command.orderId());
        confirm(command.orderId());
    }

    private void reserve(Long orderId) {
        String requestId = orderId.toString();
        orderService.reserve(orderId);

        try {
            OrderDto orderInfo = orderService.getOrder(orderId);

            ProductReserveApiRequest productReserveApiRequest = new ProductReserveApiRequest(
                    requestId,
                    orderInfo.orderItems().stream()
                            .map(orderItem -> new ProductReserveApiRequest.ReserveItem(
                                    orderItem.productId(),
                                    orderItem.quantity()
                            )).toList()
            );

            ProductReserveApiResponse productReserveApiResponse = productApiClient.reserveProduct(productReserveApiRequest);

            PointReserveApiRequest pointReserveApiRequest = new PointReserveApiRequest(
                    requestId,
                    1L,
                    productReserveApiResponse.totalPrice()
            );

            pointApiClient.reservePoint(pointReserveApiRequest);
        } catch (Exception e) {
            orderService.cancel(orderId);
            ProductReserveCancelApiRequest productReserveCancelApiRequest = new ProductReserveCancelApiRequest(requestId);

            productApiClient.cancelProduct(productReserveCancelApiRequest);

            PointReserveCancelApiRequest pointReserveCancelApiRequest = new PointReserveCancelApiRequest(requestId);

            pointApiClient.cancelPoint(pointReserveCancelApiRequest);
        }
    }

    private void confirm(Long orderId) {
        String requestId = orderId.toString();
        try {
            ProductReserveConfirmApiRequest productReserveConfirmApiRequest = new ProductReserveConfirmApiRequest(requestId);
            productApiClient.confirmProduct(productReserveConfirmApiRequest);
            PointReserveConfirmApiRequest pointReserveConfirmApiRequest = new PointReserveConfirmApiRequest(requestId);
            pointApiClient.confirmPoint(pointReserveConfirmApiRequest);

            orderService.confirm(orderId);
        } catch (Exception e) {
            orderService.pending(orderId);
            throw e;
        }
    }
}