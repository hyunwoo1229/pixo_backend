package com.pixo.pixo_website.controller;

import com.pixo.pixo_website.dto.MemberDto;
import com.pixo.pixo_website.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
