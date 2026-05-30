package com.example.tccproduct.web;

import com.example.tccproduct.application.ProductFacadeService;
import com.example.tccproduct.application.ProductService;
import com.example.tccproduct.application.RedisLockService;
import com.example.tccproduct.application.dto.ProductReserveResult;
import com.example.tccproduct.web.dto.ProductReserveRequest;
import com.example.tccproduct.web.dto.ProductReserveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductFacadeService productFacadeService;
    private final RedisLockService redisLockService;

    @PostMapping("/product/reserve")
    public ProductReserveResponse reserve(@RequestBody ProductReserveRequest request) {
        String key = "product:" + request.requestId();
        boolean acquiredLock = redisLockService.tryLock(key, request.requestId());
        if (!acquiredLock) {
            throw new IllegalStateException("락 획득에 실패했습니다.");
        }

        try {
            ProductReserveResult result = productFacadeService.tryReserve(request.toProductReserveCommand());
            return new ProductReserveResponse(result.totalPrice());
        } finally {
            redisLockService.releaseLock(key);
        }
    }
}
