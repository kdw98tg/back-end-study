package com.example.bst.jwt_example.cors.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//CORS (Cross-origin resource sharing) : 다른 도메인에서 리소스에 접근할 수 있도록 허용하는 보안 기능
//다른 도메인 예를들어서 백엔드는 8080, 프론트는 3000에서 실행되는 경우, CORS 설정이 필요하다.
//이게 필요 없으려면, spring이 열려있는 8080 포트에서 프론트도 실행되어야 한다. (같은 도메인에서 실행되어야 한다.)
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        // url 패턴별로 cors 설정을 다르게 적용할 수 있도록 해줌
        // config 의 인자로 source를 넣을 수 있음
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // config 객체 생성함
        CorsConfiguration config = new CorsConfiguration();

        // 클라이언트가 요청을 보낼 때, 쿠키나 인증 헤더 또는 세션등을 포함할 수 있도록 할것인가?
        // 이게 없으면 jwt 토큰 못씀
        config.setAllowCredentials(true);
        // 모든 출처에서 오는 요청을 전부 다 받아주겠다는 뜻 (실제 퍼블리싱 할 때는, 프론트가 열려있는 포트에서만 허용하게 해야할듯)
        config.addAllowedOriginPattern("*"); // (O) 패턴으로 모든 출처 허용
        // 모든 Http 메서드 허용
        config.addAllowedHeader("*");
        // 모든 RESTFUL 요청을 허용함
        config.addAllowedMethod("*");
        // 클라이언트가 응답을 받을 때, Authorization 헤더를 사용할 수 있도록 허용함
        config.addExposedHeader("Authorization"); // ← 이 줄을 추가!

        // 위에서 세팅한 config 규칙들을 /api/로 시작하는 모든 URL(/**)에 적용하겠다는 의미\
        // (예: /api/users, /api/login 모두 적용)
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/login", config);

        return new CorsFilter(source);
    }
}
