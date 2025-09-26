package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.MemberStatus;
import com.pixo.pixo_website.dto.*;
import com.pixo.pixo_website.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;
    //회원가입
    public ResponseEntity<?> register(MemberRequestDto dto) {
        // 아이디 길이 검증 (4~20자)
        if (dto.getLoginId().length() < 4 || dto.getLoginId().length() > 20) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("아이디는 4자 이상 20자 이하로 입력해주세요."));
        }
        // 비밀번호 길이 검증 (8~16자)
        if (dto.getPassword().length() < 8 || dto.getPassword().length() > 16) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("비밀번호는 8자 이상 16자 이하로 입력해주세요."));
        }

        if (memberRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("이미 존재하는 아이디입니다"));
        }

        if(memberRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("이미 가입된 전화번호입니다."));
        }

        boolean verified = smsService.verifyCode(dto.getPhoneNumber(), dto.getCode());
        if (!verified) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("인증번호가 일치하지 않습니다."));
        }

        Member member = new Member();
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
    public void updateExtra(MemberRequestDto dto, Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰 없음");
        }

        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("로그인한 사용자를 찾을 수 없습니다."));

        boolean verified = smsService.verifyCode(dto.getPhoneNumber(), dto.getCode());
        if (!verified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않거나 만료되었습니다.");
        }

        member.setName(dto.getName());
        member.setPhoneNumber(dto.getPhoneNumber());

        memberRepository.save(member);
    }

    //비밀번호 변경
    public void changePassword(ChangePasswordRequest req, Authentication auth) {
        String loginId = (String) auth.getPrincipal();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        if(!passwordEncoder.matches(req.getOldPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        member.setPassword(passwordEncoder.encode(req.getNewPassword()));
        memberRepository.save(member);
    }

    //아이디 중복 확인
    public SuccessResponse checkDuplicatedId(String loginId) {
        if(memberRepository.findByLoginId(loginId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다");
        }
        return new SuccessResponse("사용 가능한 아이디입니다.");
    }

    public void sendVerificationCode(String phoneNumber) {
        smsService.sendVerificationCode(phoneNumber);
    }

    //회원 탈퇴
    @Transactional
    public void deleteMember(Member member) {
        member.setStatus(MemberStatus.DELETED);
        member.setDeletedAt(LocalDateTime.now());

        member.setPassword(null);
        member.setRefreshToken(null);

        memberRepository.save(member);
    }

    //아이디 찾기 인증번호 발송
    public void sendCodeForId(String name, String phoneNumber) {
        memberRepository.findByNameAndPhoneNumber(name, phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 회원 정보가 없습니다."));

        smsService.sendVerificationCode(phoneNumber);
    }

    //아이디 찾기
    public String verifyCodeAndFindId(String name, String phoneNumber, String code) {
        boolean verified = smsService.verifyCode(phoneNumber, code);
        if (!verified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다");
        }

        Member member = memberRepository.findByNameAndPhoneNumber(name, phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 회원 정보가 없습니다."));

        //보안을 위해 아이디 일부를 마스킹 처리해서 반환
        String loginId = member.getLoginId();
        if (loginId.length() > 4) {
            return loginId.substring(0, loginId.length() - 2) + "****";
        }
        return loginId;
    }

    //비밀번호 재설정용 인증번호 발송
    public void sendCodeForPassword(String loginId, String name, String phoneNumber) {
        memberRepository.findByLoginIdAndNameAndPhoneNumber(loginId, name, phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 회원 정보가 없습니다."));
        smsService.sendVerificationCode(phoneNumber);
    }

    //비밀번호 재설정용 인증번호 확인
    public void verifyCodeForPassword(String loginId, String name, String phoneNumber, String code) {
        memberRepository.findByLoginIdAndNameAndPhoneNumber(loginId, name, phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 회원 정보가 없습니다."));
        boolean verified = smsService.verifyCode(phoneNumber, code);
        if (!verified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다");
        }
    }

    //새 비밀번호 설정
    @Transactional
    public void resetPassword(String loginId, String name, String phoneNumber, String code, String newPassword) {
        Member member = memberRepository.findByLoginIdAndNameAndPhoneNumber(loginId, name, phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 회원 정보가 없습니다."));

        if(smsService.verifyCode(phoneNumber, code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증 절차를 다시 진행해주세요.");
        }
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    public SuccessResponse checkDuplicatedPhoneNumber(String phoneNumber) {
        if(memberRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 전화번호입니다.");
        }
        return new SuccessResponse("사용 가능한 전화번호입니다.");
    }

    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 회원 정보가 없습니다."));

        return MemberInfoResponse.from(member);
    }
}
