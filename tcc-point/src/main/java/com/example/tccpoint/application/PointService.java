package com.example.tccpoint.application;

import com.example.tccpoint.application.dto.PointReserveCommand;
import com.example.tccpoint.application.dto.PointReserveConfirmCommand;
import com.example.tccpoint.domain.Point;
import com.example.tccpoint.domain.PointReservation;
import com.example.tccpoint.infrastructure.PointRepository;
import com.example.tccpoint.infrastructure.PointReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointService {
    private final PointRepository pointRepository;
    private final PointReservationRepository pointReservationRepository;

    public PointService(PointRepository pointRepository, PointReservationRepository pointReservationRepository) {
        this.pointRepository = pointRepository;
        this.pointReservationRepository = pointReservationRepository;
    }

    @Transactional
    public void tryReserve(PointReserveCommand command) {
        PointReservation reservation = pointReservationRepository.findByRequestId(command.requestId());

        if (reservation != null) {
            System.out.println("이미 예약된 요청 입니다.");
            return;
        }

        Point point = pointRepository.findByUserId(command.userId());

        point.reserve(command.reserveAmount());
        pointReservationRepository.save(new PointReservation(
                command.requestId(),
                point.getId(),
                command.reserveAmount()
        ));
    }

    @Transactional
    public void confirmReserve(PointReserveConfirmCommand command) {
        PointReservation reservation = pointReservationRepository.findByRequestId(command.requestId());

        if (reservation == null) {
            throw new RuntimeException("예약내역이 존재하지 않습니다.");
        }

        if (reservation.getStatus() == PointReservation.PointReservationStatus.CONFIRMED) {
            System.out.println("이미 확정된 예약입니다.");
            return;
        }

        Point point = pointRepository.findById(reservation.getPointId()).orElseThrow();

        point.confirm(reservation.getReservedAmount());
        reservation.confirm();

        pointRepository.save(point);
        pointReservationRepository.save(reservation);
    }
}
