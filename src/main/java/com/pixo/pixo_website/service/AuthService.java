package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.MemberStatus;
import com.pixo.pixo_website.dto.ErrorResponse;
import com.pixo.pixo_website.dto.MemberRequestDto;
import com.pixo.pixo_website.dto.SuccessResponse;
import com.pixo.pixo_website.dto.TokenResponseDto;
import com.pixo.pixo_website.repository.MemberRepository;
import com.pixo.pixo_website.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Map<String, Object> login(MemberRequestDto dto) {
        Member member = memberRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 아이디입니다"));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이미 탈퇴 처리된 계정입니다.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getLoginId(), member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getLoginId(), member.getId());

        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("name", member.getName());
        result.put("role", member.getRole().name());

        return result;
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null) {
            try {
                String loginId = jwtTokenProvider.getLoginId(token);
                memberRepository.findByLoginId(loginId).ifPresent(member -> {
                    member.updateRefreshToken(null);
                });
            } catch (Exception e) {
                // 로그아웃 시 토큰 만료 등은 무시
            }
        }
    }

    @Transactional
    public TokenResponseDto reissue(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token 입니다.");
        }

        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "일치하는 사용자가 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(member.getLoginId(), member.getId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getLoginId(), member.getId());

        member.updateRefreshToken(newRefreshToken);
        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }
}