package com.example.sagaorchestrationproduct.infrastructure;

import com.example.sagaorchestrationproduct.domain.ProductTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductTransactionHistoryRepository extends JpaRepository<ProductTransactionHistory, Long> {
    List<ProductTransactionHistory> findAllByRequestIdAndTransactionType(
            String requestId,
            ProductTransactionHistory.TransactionType transactionType
    );
}