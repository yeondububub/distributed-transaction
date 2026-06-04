package com.example.sagaorchestrationproduct.application;

import com.example.sagaorchestrationproduct.application.dto.ProductBuyCommand;
import com.example.sagaorchestrationproduct.application.dto.ProductBuyResult;
import com.example.sagaorchestrationproduct.domain.Product;
import com.example.sagaorchestrationproduct.domain.ProductTransactionHistory;
import com.example.sagaorchestrationproduct.infrastructure.ProductRepository;
import com.example.sagaorchestrationproduct.infrastructure.ProductTransactionHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductTransactionHistoryRepository productTransactionHistoryRepository;

    public ProductService(ProductRepository productRepository, ProductTransactionHistoryRepository productTransactionHistoryRepository) {
        this.productRepository = productRepository;
        this.productTransactionHistoryRepository = productTransactionHistoryRepository;
    }

    public ProductBuyResult buy(ProductBuyCommand command) {
        List<ProductTransactionHistory> histories = productTransactionHistoryRepository.findAllByRequestIdAndTransactionType(
                command.requestId(),
                ProductTransactionHistory.TransactionType.PURCHASE
        );

        if(!histories.isEmpty()) {
            System.out.println("이미 구매한 기록이 있습니다.");

            Long totalPrice = histories
                    .stream()
                    .mapToLong(ProductTransactionHistory::getPrice)
                    .sum();

            return new ProductBuyResult(totalPrice);
        }

        Long totalPrice = 0L;

        for (ProductBuyCommand.ProductInfo productInfo : command.productInfos()) {
            Product product = productRepository.findById(productInfo.productId()).orElseThrow();

            product.buy(productInfo.quantity());
            Long price = product.calculatePrice(productInfo.quantity());
            totalPrice += price;

            productTransactionHistoryRepository.save(
                    new ProductTransactionHistory(
                            command.requestId(),
                            productInfo.productId(),
                            productInfo.quantity(),
                            price,
                            ProductTransactionHistory.TransactionType.PURCHASE
                    )
            );
        }

        return new ProductBuyResult(totalPrice);
    }
}
