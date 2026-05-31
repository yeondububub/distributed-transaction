package com.example.tccproduct.application;

import com.example.tccproduct.application.dto.ProductReserveCommand;
import com.example.tccproduct.application.dto.ProductReserveConfirmCommand;
import com.example.tccproduct.application.dto.ProductReserveResult;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
public class ProductFacadeService {
    private final ProductService productService;

    public ProductFacadeService(ProductService productService) {
        this.productService = productService;
    }

    public ProductReserveResult tryReserve(ProductReserveCommand commend) {
        int tryCount = 0;

        while (tryCount < 3) {
            try {
                return productService.tryReserve(commend);
            } catch (ObjectOptimisticLockingFailureException e) {
                tryCount++;
            }
        }

        throw new RuntimeException("예약에 실패했습니다.");
    }

    public void confirmReserve(ProductReserveConfirmCommand commend) {
        int tryCount = 0;

        while (tryCount < 3) {
            try {
                productService.confirmReserve(commend);
                return ;
            } catch (ObjectOptimisticLockingFailureException e) {
                tryCount++;
            }
        }

        throw new RuntimeException("예약에 실패했습니다.");
    }
}
