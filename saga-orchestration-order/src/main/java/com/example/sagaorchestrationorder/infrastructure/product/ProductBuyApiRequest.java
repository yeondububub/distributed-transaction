package com.example.sagaorchestrationorder.infrastructure.product;

import java.util.List;

public record ProductBuyApiRequest(
        String requestId,
        List<ProductInfo> productInfos
) {

    public record ProductInfo(
            Long productId,
            Long quantity
    ) { }
}
