package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.dto.*;
import com.pixo.pixo_website.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor

public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;

    //회원가입
    @Transactional
    public void register(MemberRequestDto dto) {
        if (dto.getLoginId().length() < 4 || dto.getLoginId().length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디는 4자 이상 20자 이하로 입력해주세요.");
        }
        if (dto.getPassword().length() < 8 || dto.getPassword().length() > 16) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상 16자 이하로 입력해주세요.");
        }

        if (memberRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다");
        }

        if(memberRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 전화번호입니다.");
        }

        boolean verified = smsService.verifyCode(dto.getPhoneNumber(), dto.getCode());
        if (!verified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다.");
        }

        Member member = new Member();
        member.setLoginId(dto.getLoginId());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setName(dto.getName());
        member.setPhoneNumber(dto.getPhoneNumber());
        member.setProvider("form");
        memberRepository.save(member);
    }

    //소셜 로그인 후 추가 정보 저장
    @Transactional
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
    @Transactional
    public void changePassword(ChangePasswordRequest req, Authentication auth) {
        String loginId = auth.getName();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 사용자입니다."));

        if(!passwordEncoder.matches(req.getOldPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        }

        String newPassword = req.getNewPassword();
        if(newPassword.length() < 8 || newPassword.length() > 16) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상 16자 이하로 입력해주세요.");
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
        member.withdraw();
        memberRepository.save(member);
    }

    //아이디 찾기 인증번호 발송
    @Transactional
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

        return member.getMaskedLoginId();
    }

    //비밀번호 재설정용 인증번호 발송
    @Transactional
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

    @Transactional
    public SuccessResponse checkDuplicatedPhoneNumber(String phoneNumber) {
        if(memberRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 전화번호입니다.");
        }
        return new SuccessResponse("사용 가능한 전화번호입니다.");
    }

    public MemberInfoResponse getMemberInfoByAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        String loginId = authentication.getName();

        if (loginId == null || loginId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보를 확인할 수 없습니다.");
        }
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 로그인 ID의 회원을 찾을 수 없습니다."
                ));

        return MemberInfoResponse.from(member);
    }
}
