package com.pixo.pixo_website.controller;

import com.pixo.pixo_website.dto.ChangePasswordRequest;
import com.pixo.pixo_website.dto.MemberDto;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor

public class MemberController {

    private final MemberService memberService;

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody MemberDto dto) {
        return memberService.register(dto);
    }

    //소셜 로그인 후 추가 정보 저장
    @PostMapping("/update-extra")
    public ResponseEntity<?> updateExtra(@RequestBody MemberDto dto, Authentication authentication) {

        memberService.updateExtra(dto, authentication);
        return ResponseEntity.ok(new SuccessResponse("추가 정보 업데이트 완료"));
    }

    //비밀번호 변경
    @PutMapping("/profile/password")
    public ResponseEntity<Void> updatePassword(@RequestBody ChangePasswordRequest req, Authentication authentication) {
        memberService.changePassword(req, authentication);
        return ResponseEntity.ok().build();
    }
}
