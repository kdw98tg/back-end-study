package com.example.bst.oauth2_sequrity_example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.bst.oauth2_sequrity_example.oauth2.service.PrincipalOauth2UserService;
import com.example.bst.oauth2_sequrity_example.user.data.UserRole;

import lombok.RequiredArgsConstructor;

// 시큐리티에서 일단 bean 으로 설정을 올리면
// 기본설정은 무시되고 내가 설정한 설정들이 올라감
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;


    // 해당 메서드의 리턴되는 오브젝트를 IoC 로 등록해줌
    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity _http) throws Exception {
        _http
                .csrf((csrfConfig) -> csrfConfig.disable())

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/user/**").authenticated()// user 는 인증된 사용자만
                        .requestMatchers("/manager/**")
                        .hasAnyRole(UserRole.MANAGER.toString(), UserRole.ADMIN.toString())// manager는 admin이나 manager 인
                                                                                           // 사람만
                        .requestMatchers("/admin/**").hasRole(UserRole.ADMIN.toString())// admin은 role manager인 사람만
                        .anyRequest().permitAll())// 위에서 명시한 주소 외에 나머지 주소는 모두 허용

                .formLogin(form -> form
                        .loginPage("/login")// 로그인이 필요하면 /login 경로의 페이지를 보여주고
                        .loginProcessingUrl("/loginProc")// 로그인을 처리하는 라우팅은 다음과 같고
                        .defaultSuccessUrl("/")// 끝나면 / 로 가라
                        .permitAll())

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(principalOauth2UserService)));
        return _http.build();
    }
}

// CSRF 란 (Cross Site Request Foregery)
// 사이트간 요청 위조
// 웹 취약점 중 하나임
// 공격자가 희생자의 권한을 도용하여 특정 웹 사이트의 기능을 실행하는걸 말함
// 세션을 쓸때는 공격자가 세션 정보를 탈취할 수 있기 때문에, csrf 설정을 끄면 안되는데, jwt를 사용하는 stateless 한 서버는
// csrf를 꺼도 무방함

// 웹 브라우저는 기본적으로 보안을 위해 '동일 출처 정책(SOP)'을 따릅니다.
// 즉, 프론트엔드(http://localhost:3000)에서 백엔드(http://localhost:8080)로 API 요청을 보내면,
// 포트 번호(출처)가 다르기 때문에 브라우저가 알아서 이 요청을 차단해 버립니다.
// 그래서 설정을 통해서, 3000번에서 열린 서비스에서 오는 요청은 안전하니까 받아줘 라는 설정을 해줘야 함