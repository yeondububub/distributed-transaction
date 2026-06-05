package com.example.sagaorchestrationpoint.application;

import com.example.sagaorchestrationpoint.application.dto.PointUseCancelCommand;
import com.example.sagaorchestrationpoint.application.dto.PointUseCommand;
import com.example.sagaorchestrationpoint.domain.Point;
import com.example.sagaorchestrationpoint.domain.PointTransactionHistory;
import com.example.sagaorchestrationpoint.infrastructure.PointRepository;
import com.example.sagaorchestrationpoint.infrastructure.PointTransactionHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointService {

    private final PointRepository pointRepository;
    private final PointTransactionHistoryRepository pointTransactionHistoryRepository;

    public PointService(PointRepository pointRepository, PointTransactionHistoryRepository pointTransactionHistoryRepository) {
        this.pointRepository = pointRepository;
        this.pointTransactionHistoryRepository = pointTransactionHistoryRepository;
    }

    @Transactional
    public void use(PointUseCommand command) {
        PointTransactionHistory useHistory = pointTransactionHistoryRepository.findByRequestIdAndTransactionType(
                command.requestId(),
                PointTransactionHistory.TransactionType.USE
        );

        if (useHistory != null) {
            System.out.println("이미 사용한 이력이 있습니다.");
            return;
        }

        Point point = pointRepository.findByUserId(command.userId());

        if  (point == null) {
            throw new RuntimeException("포인트가 존재하지 않습니다.");
        }

        point.use(command.amount());
        pointTransactionHistoryRepository.save(new PointTransactionHistory(
                command.requestId(),
                point.getId(),
                command.amount(),
                PointTransactionHistory.TransactionType.USE
        ));
    }

    @Transactional
    public void cancel(PointUseCancelCommand command) {
        PointTransactionHistory useHistory = pointTransactionHistoryRepository.findByRequestIdAndTransactionType(
                command.requestId(),
                PointTransactionHistory.TransactionType.USE
        );

        if (useHistory == null) {
            throw new RuntimeException("포인트 사용내역이 없습니다.");
        }

        PointTransactionHistory cancelHistory = pointTransactionHistoryRepository.findByRequestIdAndTransactionType(
                command.requestId(),
                PointTransactionHistory.TransactionType.CANCEL
        );

        if (cancelHistory != null) {
            System.out.println("이미 취소된 요청입니다.");
            return;
        }

        Point point = pointRepository.findById(useHistory.getPointId()).orElseThrow();

        point.cancel(useHistory.getAmount());
        pointTransactionHistoryRepository.save(new PointTransactionHistory(
                command.requestId(),
                point.getId(),
                useHistory.getAmount(),
                PointTransactionHistory.TransactionType.CANCEL
        ));
    }
}
