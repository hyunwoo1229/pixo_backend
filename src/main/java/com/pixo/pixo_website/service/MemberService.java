package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.MemberEntity;
import com.pixo.pixo_website.dto.ChangePasswordRequest;
import com.pixo.pixo_website.dto.ErrorResponse;
import com.pixo.pixo_website.dto.MemberDto;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor

public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    public ResponseEntity<?> register(MemberDto dto) {
        if (memberRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("이미 존재하는 아이디입니다"));
        }

        MemberEntity member = new MemberEntity();
        member.setLoginId(dto.getLoginId());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setName(dto.getName());
        member.setPhoneNumber(dto.getPhoneNumber());
        member.setProvider("form");
        memberRepository.save(member);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponse("회원가입 성공"));
    }

    //소셜 로그인 후 추가 정보 저장
    public void updateExtra(MemberDto dto, Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰 없음");
        }

        String loginId = authentication.getName();

        MemberEntity member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("로그인한 사용자를 찾을 수 없습니다."));

        member.setName(dto.getName());
        member.setPhoneNumber(dto.getPhoneNumber());

        memberRepository.save(member);
    }

    //비밀번호 변경
    public void changePassword(ChangePasswordRequest req, Authentication auth) {
        String loginId = (String) auth.getPrincipal();
        MemberEntity member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        if(!passwordEncoder.matches(req.getOldPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        member.setPassword(passwordEncoder.encode(req.getNewPassword()));
        memberRepository.save(member);
    }

}
