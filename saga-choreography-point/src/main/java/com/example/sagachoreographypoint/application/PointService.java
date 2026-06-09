package com.example.sagachoreographypoint.application;

import com.example.sagachoreographypoint.application.dto.PointUseCancelCommand;
import com.example.sagachoreographypoint.application.dto.PointUseCommand;
import com.example.sagachoreographypoint.domain.Point;
import com.example.sagachoreographypoint.domain.PointTransactionHistory;
import com.example.sagachoreographypoint.infrastructure.PointRepository;
import com.example.sagachoreographypoint.infrastructure.PointTransactionHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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

        // TODO: 강제 예외 발생
        if (Integer.valueOf(command.requestId()) % 2 == 0) {
            System.out.println("===========================");
            throw new RuntimeException("=== 강제 예외 발생!!! ===");
        }
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
