package com.pixo.pixo_website.controller;

import com.pixo.pixo_website.dto.MemberRequestDto;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.dto.TokenResponseDto;
import com.pixo.pixo_website.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody MemberRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new SuccessResponse("로그아웃 완료"));
    }

    //refreshToken으로 accessToken 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.reissue(body.get("refreshToken")));
    }
}