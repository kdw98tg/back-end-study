package com.example.bst.jwt_example.jwt.provider;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long expirationMs) {
        this.expirationMs = expirationMs;
        // 1. 서명에 사용할 알고리즘 설정
        algorithm = Algorithm.HMAC512(secretKey);
    }

    public String generateToken(String username, String role) {

        // 2. 토큰 만료 시간 설정
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationMs);

        // 3. JWT 토큰 생성
        String token = JWT.create()
                .withIssuer("comok") // 발급자 (iss)
                .withSubject(username) // 토큰 대상자 (sub) - 보통 유저의 고유 ID나 이메일
                .withIssuedAt(now) // 발급 시간 (iat)
                .withExpiresAt(validity) // 만료 시간 (exp)
                .withClaim("role", role) // 커스텀 클레임 (사용자 권한 등 추가 정보)
                // .withClaim("isPremium", true) // 데이터 타입(Boolean, Integer, List 등)을 다양하게 넣을 수
                // 있습니다.
                .sign(algorithm);

        return token;
    }

    public String validateToken(String _token) {
        return JWT.require(algorithm).build().verify(_token).getSubject();

    }
}
