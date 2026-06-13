package com.example.sagachoreographyproduct.application;

import com.example.sagachoreographyproduct.application.dto.ProductBuyCancelCommand;
import com.example.sagachoreographyproduct.application.dto.ProductBuyCancelResult;
import com.example.sagachoreographyproduct.application.dto.ProductBuyCommand;
import com.example.sagachoreographyproduct.application.dto.ProductBuyResult;
import com.example.sagachoreographyproduct.domain.Product;
import com.example.sagachoreographyproduct.domain.ProductTransactionHistory;
import com.example.sagachoreographyproduct.infrastructure.ProductRepository;
import com.example.sagachoreographyproduct.infrastructure.ProductTransactionHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductTransactionHistoryRepository productTransactionHistoryRepository;

    public ProductService(ProductRepository productRepository, ProductTransactionHistoryRepository productTransactionHistoryRepository) {
        this.productRepository = productRepository;
        this.productTransactionHistoryRepository = productTransactionHistoryRepository;
    }

    @Transactional
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

    @Transactional
    public ProductBuyCancelResult cancel(ProductBuyCancelCommand command) {
        List<ProductTransactionHistory> buyHistories = productTransactionHistoryRepository.findAllByRequestIdAndTransactionType(
                command.requestId(),
                ProductTransactionHistory.TransactionType.PURCHASE
        );

        if (buyHistories.isEmpty()) {
            System.out.println("구매 이력이 존재하지 않아 취소 처리를 생략합니다. (requestId: " + command.requestId() + ")");
            return new ProductBuyCancelResult(0L);
        }

        // 강제 예외 테스트: 취소할 수량이 88개인 경우 강제 예외 발생 (보상 트랜잭션 자체 실패 테스트)
        for (ProductTransactionHistory history : buyHistories) {
            if (history.getQuantity() == 88L) {
                throw new RuntimeException("[강제 예외] 상품 서비스 보상 트랜잭션(취소) 중 오류 발생");
            }
        }

        List<ProductTransactionHistory> cancelHistories = productTransactionHistoryRepository.findAllByRequestIdAndTransactionType(
                command.requestId(),
                ProductTransactionHistory.TransactionType.CANCEL
        );

        if (!cancelHistories.isEmpty()) {
            System.out.println("이미 취소 되었습니다.");
            long totalPrice = cancelHistories.stream()
                    .mapToLong(ProductTransactionHistory::getPrice)
                    .sum();
            return new ProductBuyCancelResult(totalPrice);
        }

        Long totalPrice = 0L;

        for (ProductTransactionHistory history : buyHistories) {
            Product product = productRepository.findById(history.getProductId()).orElseThrow();

            product.cancel(history.getQuantity());
            totalPrice += history.getPrice();

            productTransactionHistoryRepository.save(
                    new ProductTransactionHistory(
                            command.requestId(),
                            history.getProductId(),
                            history.getQuantity(),
                            history.getPrice(),
                            ProductTransactionHistory.TransactionType.CANCEL

                    )
            );
        }

        return new ProductBuyCancelResult(totalPrice);
    }
}
