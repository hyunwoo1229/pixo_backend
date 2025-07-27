package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.MemberEntity;
import com.pixo.pixo_website.dto.ErrorResponse;
import com.pixo.pixo_website.dto.MemberDto;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        memberRepository.save(member);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponse("회원가입 성공"));
    }
}
