package com.example.sagachoreographypoint.infrastructure;

import com.example.sagachoreographypoint.domain.PointTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionHistoryRepository extends JpaRepository<PointTransactionHistory, Long> {
    PointTransactionHistory findByRequestIdAndTransactionType(
            String requestId,
            PointTransactionHistory.TransactionType transactionType
    );
}
