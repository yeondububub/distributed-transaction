package com.example.tccpoint.controller;

import com.example.tccpoint.application.PointFacadeService;
import com.example.tccpoint.application.RedisLockService;
import com.example.tccpoint.controller.dto.PointReserveConfirmRequest;
import com.example.tccpoint.controller.dto.PointReserveRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class PointController {

    private final PointFacadeService pointFacadeService;
    private final RedisLockService redisLockService;

    public PointController(PointFacadeService pointFacadeService, RedisLockService redisLockService) {
        this.pointFacadeService = pointFacadeService;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/point/reserve")
    public void reserve(@RequestBody PointReserveRequest request) {
        String key = "point:" + request.requestId();
        boolean acquiredLock = redisLockService.tryLock(key, request.requestId());

        if (!acquiredLock) {
            throw new RuntimeException("락 획득에 실패하였습니다.");
        }

        try {
            pointFacadeService.tryReserve(request.toCommand());
        } finally {
            redisLockService.releaseLock(key);
        }
    }

    @PostMapping("/point/confirm")
    public void confirm(@RequestBody PointReserveConfirmRequest request) {
        String key = "point:" + request.requestId();
        boolean acquiredLock = redisLockService.tryLock(key, request.requestId());

        if (!acquiredLock) {
            throw new RuntimeException("락 획득에 실패하였습니다.");
        }

        try {
            pointFacadeService.confirmReserve(request.toCommand());
        } finally {
            redisLockService.releaseLock(key);
        }
    }

}
