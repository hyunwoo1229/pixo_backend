package com.pixo.pixo_website.service;

import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor

public class SmsService {
    private final Map<String, VerificationInfo> verificationCodes = new ConcurrentHashMap<>();

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.from-number}")
    private String fromNumber;

    // 유효시간 (예: 5분)
    private static final Duration VALID_DURATION = Duration.ofMinutes(5);

    // 내부 클래스: 인증코드 + 생성시각
    private static class VerificationInfo {
        String code;
        LocalDateTime issuedAt;

        VerificationInfo(String code, LocalDateTime issuedAt) {
            this.code = code;
            this.issuedAt = issuedAt;
        }
    }
    public void sendVerificationCode(String phoneNumber) {
        String code = generateCode();
        verificationCodes.put(phoneNumber, new VerificationInfo(code, LocalDateTime.now()));

        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret,  "https://api.solapi.com");

        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(phoneNumber);
        message.setText("[PIXO] 인증번호는 " + code + "입니다");

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public boolean verifyCode(String phoneNumber, String inputCode) {
        VerificationInfo info = verificationCodes.get(phoneNumber);
        if (info == null) return false;

        // 유효시간 초과 확인
        if (Duration.between(info.issuedAt, LocalDateTime.now()).compareTo(VALID_DURATION) > 0) {
            verificationCodes.remove(phoneNumber); // 시간 초과 시 삭제
            return false;
        }

        boolean match = info.code.equals(inputCode);
        if (match) {
            verificationCodes.remove(phoneNumber); // 인증 성공 시 삭제
        }

        return match;
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
