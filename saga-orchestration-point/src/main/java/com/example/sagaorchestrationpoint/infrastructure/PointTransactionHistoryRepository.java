package com.example.sagaorchestrationpoint.infrastructure;

import com.example.sagaorchestrationpoint.domain.PointTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionHistoryRepository extends JpaRepository<PointTransactionHistory, Long> {
    PointTransactionHistory findByRequestIdAndTransactionType(
            String requestId,
            PointTransactionHistory.TransactionType transactionType
    );
}
