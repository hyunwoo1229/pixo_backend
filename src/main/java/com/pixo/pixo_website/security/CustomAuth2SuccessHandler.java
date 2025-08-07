package com.pixo.pixo_website.security;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.repository.MemberRepository;
import com.pixo.pixo_website.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid OAuth2 token.");
            return;
        }

        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId(); // "google", "naver", "kakao"
        String loginId = extractLoginId(oAuth2User, provider);

        // 사용자 정보가 DB에 없다면 회원가입 처리
        Optional<Member> existing = memberRepository.findByLoginId(loginId);
        Member user = existing.orElseGet(() -> {
            Member newMember = new Member();
            newMember.setLoginId(loginId);
            newMember.setPassword(null);
            newMember.setName("betaName"); //나중에 수정해야됨
            newMember.setPhoneNumber(null);
            newMember.setProvider(provider);
            return memberRepository.save(newMember);
        });

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId());

        //DB에 RefreshToken 저장
        user.updateRefreshToken(refreshToken);
        memberRepository.save(user);

        if(user.getName() == null || user.getPhoneNumber() == null) {
            String redirectUrl = UriComponentsBuilder
                    .fromUriString("http://localhost:5173/social-extra")
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .queryParam("name", URLEncoder.encode(user.getName(), StandardCharsets.UTF_8))
                    .build()
                    .toUriString();
            response.sendRedirect(redirectUrl);
            return;
        }

        // 리다이렉트할 URL (프론트에 토큰 전달)
        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth-success")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("name", URLEncoder.encode(user.getName(), StandardCharsets.UTF_8))
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private String extractLoginId(OAuth2User oAuth2User, String provider) {
        if ("google".equals(provider)) {
            String email = oAuth2User.getAttribute("email");
            return "google_" + email;
        }

        if ("naver".equals(provider)) {
            Map<String, Object> response = oAuth2User.getAttribute("response");
            String email = (String) response.get("email"); // ✅ 제대로 꺼냄
            if (email != null) {
                return "naver_" + email;
            } else {
                return "naver_" + response.get("id"); // ✅ fallback
            }
        }

        if ("kakao".equals(provider)) {
            Map<String, Object> account = oAuth2User.getAttribute("kakao_account");
            String email = (String) account.get("email");
            if (email != null) {
                return "kakao_" + email;
            } else {
                Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                String nickname = (String) profile.get("nickname");
                return "kakao_" + nickname; // ✅ fallback
            }
        }

        return provider + "_unknown";
    }

}

