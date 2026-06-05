package com.example.sagaorchestrationorder.controller;

import com.example.sagaorchestrationorder.application.OrderService;
import com.example.sagaorchestrationorder.application.dto.CreateOrderResult;
import com.example.sagaorchestrationorder.controller.dto.CreateOrderRequest;
import com.example.sagaorchestrationorder.controller.dto.CreateOrderResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResult result = orderService.createOrder(request.toCommand());

        return new CreateOrderResponse(result.orderId());
    }
}
