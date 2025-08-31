package com.pixo.pixo_website.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private final Map<String, LocalDateTime> requestTimestamps = new ConcurrentHashMap<>();

    public void check(String key, Duration cooldown) {
        LocalDateTime lastRequestTime = requestTimestamps.get(key);
        if (lastRequestTime != null && Duration.between(lastRequestTime, LocalDateTime.now()).compareTo(cooldown) < 0) {
            long remainingSeconds = cooldown.getSeconds() - Duration.between(lastRequestTime, LocalDateTime.now()).getSeconds();
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "너무 잦은 요청입니다." + remainingSeconds + "초 후에 다시 시도해주세요.");
        }
        requestTimestamps.put(key, LocalDateTime.now());
    }
}
