package com.example.sagachoreographypoint.controller;

import com.example.sagachoreographypoint.application.PointService;
import com.example.sagachoreographypoint.application.RedisLockService;
import com.example.sagachoreographypoint.controller.dto.PointUseCancelRequest;
import com.example.sagachoreographypoint.controller.dto.PointUseRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointController {

    private final PointService pointService;
    private final RedisLockService redisLockService;

    public PointController(PointService pointService, RedisLockService redisLockService) {
        this.pointService = pointService;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/point/use")
    public void use(@RequestBody PointUseRequest request) {
        String lockKey = "point:orchestration:" + request.requestId();

        boolean lockAcquired = redisLockService.tryLock(lockKey, request.requestId());

        if (!lockAcquired) {
            throw new RuntimeException("락 획득에 실패했습니다.");
        }
        try {
            pointService.use(request.toCommand());
        } finally {
            redisLockService.releaseLock(lockKey);
        }

    }

    @PostMapping("/point/use/cancel")
    public void cancel(@RequestBody PointUseCancelRequest request) {
        String lockKey = "point:orchestration:" + request.requestId();

        boolean lockAcquired = redisLockService.tryLock(lockKey, request.requestId());

        if (!lockAcquired) {
            throw new RuntimeException("락 획득에 실패하였습니다.");
        }

        try {
            pointService.cancel(request.toCommand());
        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }
}
