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

        // 강제 예외 테스트: 결제 금액이 9900원(보상 트랜잭션 성공 테스트) 또는 8800원(보상 트랜잭션 실패 테스트)인 경우 강제 예외 발생
        if (command.amount() == 9900L || command.amount() == 8800L) {
            throw new RuntimeException("[강제 예외] 포인트 서비스 사용 중 오류 발생 (금액: " + command.amount() + ")");
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
            System.out.println("포인트 사용 내역이 존재하지 않아 취소 처리를 생략합니다. (requestId: " + command.requestId() + ")");
            return;
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
