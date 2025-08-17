package com.pixo.pixo_website.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${keySecret}")
    private String keySecret;
    private final long ACCESS_TOKEN_EXPIRATION_MS = 36000000L;
    private final long REFRESH_TOKEN_EXPIRATION_MS = 1209600000L; // 2주
    private Key key;

    @PostConstruct
    protected void init() {
        byte[] secretBytes = Base64.getEncoder().encode(keySecret.getBytes());
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * AccessToken 생성 시 memberId를 추가로 받습니다.
     * @param loginId 사용자 로그인 ID
     * @param memberId 사용자 숫자 ID (PK)
     * @return 생성된 AccessToken
     */
    public String createAccessToken(String loginId, Long memberId) {
        return createToken(loginId, memberId, ACCESS_TOKEN_EXPIRATION_MS);
    }

    /**
     * RefreshToken 생성 시 memberId를 추가로 받습니다.
     * @param loginId 사용자 로그인 ID
     * @param memberId 사용자 숫자 ID (PK)
     * @return 생성된 RefreshToken
     */
    public String createRefreshToken(String loginId, Long memberId) {
        return createToken(loginId, memberId, REFRESH_TOKEN_EXPIRATION_MS);
    }

    /**
     * JWT를 생성합니다.
     * claim에 "memberId" 키로 사용자의 숫자 ID를 추가합니다.
     */
    private String createToken(String loginId, Long memberId, long expirationMs) {
        return Jwts.builder()
                .setSubject(loginId)
                .claim("memberId", memberId) // 토큰에 memberId를 추가하는 핵심 로직
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String getLoginId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (SecurityException | MalformedJwtException e) {
            System.err.println("Invalid JWT Token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Expired JWT Token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Unsupported JWT Token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("JWT 파싱 실패: " + e.getMessage());
        }
    }
}
