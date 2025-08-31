package com.pixo.pixo_website.controller;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.dto.ChangePasswordRequest;
import com.pixo.pixo_website.dto.MemberRequestDto;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.repository.MemberRepository;
import com.pixo.pixo_website.security.CumstomUserDetails;
import com.pixo.pixo_website.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor

public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody MemberRequestDto dto) {
        return memberService.register(dto);
    }

    //소셜 로그인 후 추가 정보 저장
    @PostMapping("/update-extra")
    public ResponseEntity<SuccessResponse> updateExtra(@RequestBody MemberRequestDto dto, Authentication authentication) {
        memberService.updateExtra(dto, authentication);
        return ResponseEntity.ok(new SuccessResponse("추가 정보 업데이트 완료"));
    }

    //인증 코드 전송
    @PostMapping("/send-code")
    public ResponseEntity<SuccessResponse> sendCode(@RequestParam String phoneNumber) {
        memberService.sendVerificationCode(phoneNumber);
        return ResponseEntity.ok(new SuccessResponse("인증번호가 전송되었습니다."));
    }

    //비밀번호 변경
    @PutMapping("/profile/password")
    public ResponseEntity<SuccessResponse> updatePassword(@RequestBody ChangePasswordRequest req, Authentication authentication) {
        memberService.changePassword(req, authentication);
        return ResponseEntity.ok(new SuccessResponse("비밀번호가 변경되었습니다."));
    }

    //아이디 중복 확인
    @GetMapping("/check-id")
    public ResponseEntity<?> checkDuplicatedId(@RequestParam String loginId) {
        return ResponseEntity.ok(memberService.checkDuplicatedId(loginId));
    }

    //회원 탈퇴
    @DeleteMapping
    public ResponseEntity<SuccessResponse> deleteMember(@AuthenticationPrincipal CumstomUserDetails userDetails) {
        Member member = userDetails.getMember();
        memberService.deleteMember(member);
        return ResponseEntity.ok(new SuccessResponse("회원 탈퇴가 완료되었습니다."));
    }

    //아이디 찾기 인증 코드 전송
    @PostMapping("find-id/send-code")
    public ResponseEntity<SuccessResponse> findIdSendCode(@RequestBody MemberRequestDto dto) {
        memberService.sendCodeForId(dto.getName(), dto.getPhoneNumber());
        return ResponseEntity.ok(new SuccessResponse("인증번호가 전송되었습니다."));
    }

    //아이디 찾기 인증번호 확인 및 아이디 반환
    @PostMapping("/find-id/verify")
    public ResponseEntity<Map<String, String>> findIdVerify(@RequestBody MemberRequestDto dto) {
        String foundId = memberService.verifyCodeAndFindId(dto.getName(), dto.getPhoneNumber(), dto.getCode());
        Map<String, String> response = new HashMap<>();
        response.put("loginId", foundId);
        return ResponseEntity.ok(response);
    }
}
