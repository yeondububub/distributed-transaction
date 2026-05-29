package com.example.tccproduct.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RedisLockService {
    private static final Logger log = LoggerFactory.getLogger(RedisLockService.class);
    private final StringRedisTemplate redisTemplate;

    public RedisLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryLock(String key, String value) {
        log.info("try lock for key: {}, value: {}", key, value);
        return Objects.nonNull(redisTemplate.opsForValue().setIfAbsent(key, value));
    }

    public void releaseLock(String key) {
        log.info("release lock for key: {}", key);
        redisTemplate.delete(key);
    }
}
