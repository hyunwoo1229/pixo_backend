package com.pixo.pixo_website.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        // 우리가 원하는 JSON 형태를 직접 만들어서 반환합니다.
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getStatusCode().value());
        body.put("message", ex.getReason()); // 예외에 포함된 상세 메시지("~초 후에...")를 message 필드에 담습니다.

        return new ResponseEntity<>(body, ex.getStatusCode());
    }
}