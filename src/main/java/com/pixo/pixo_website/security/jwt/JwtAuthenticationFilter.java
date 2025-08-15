package com.pixo.pixo_website.security.jwt;

import com.pixo.pixo_website.domain.Member;          // ← 프로젝트 패키지에 맞게 수정
import com.pixo.pixo_website.repository.MemberRepository; // ← 패키지 경로 맞추기

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository; // ✅ DB에서 role 로드

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            if (!jwtTokenProvider.validateToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid/Expired Token");
                return;
            }

            // 토큰에서 사용자 식별자 꺼냄
            String loginId = jwtTokenProvider.getLoginId(token);

            // ✅ DB에서 role 로드 → ROLE_ 프리픽스 붙여 GrantedAuthority 생성
            List<GrantedAuthority> authorities = Collections.emptyList();
            Member member = memberRepository.findByLoginId(loginId).orElse(null);
            if (member != null && member.getRole() != null) {
                String roleName = member.getRole().name();           // 예: ADMIN
                authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleName)); // ROLE_ADMIN
            }

            // ✅ 권한을 포함한 Authentication을 컨텍스트에 주입
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(loginId, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}
