package com.example.bst.jwt_example.jwt.filter.authorization;

import java.io.IOException;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.bst.jwt_example.auth.model.PrincipalDetails;
import com.example.bst.jwt_example.jwt.data.JwtData;
import com.example.bst.jwt_example.jwt.provider.JwtTokenProvider;
import com.example.bst.jwt_example.user.entity.User;
import com.example.bst.jwt_example.user.repository.IUserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

//시큐리티가 filter를 가지고 있는데, 그 필터중에 BasicAuthenticationFilter 라는 필터가 있다.
//이 필터는 모든 요청이 올 때마다, 요청에 JWT 토큰이 있는지 확인하는 필터이다.
//만약 JWT 토큰이 있으면, 토큰이 유효한지 검증
//유효하면, Authentication 객체를 만들어서 시큐리티 컨텍스트에 저장하는 역할을 한다.
//만약 권한이나 인증이 필요한 주소가 아니라면 이 필터를 안탐
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final IUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(AuthenticationManager _authenticationManager, IUserRepository _userRepository,
            JwtTokenProvider jwtTokenProvider) {
        super(_authenticationManager);
        this.userRepository = _userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 형 변환
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 에러 재디스패치는 로그 없이 바로 통과
        if ("ERROR".equals(httpRequest.getDispatcherType().name())) {
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("[JWT filter] " + httpRequest.getMethod() + " " + httpRequest.getRequestURI());

        String jwtHeader = httpRequest.getHeader("Authorization");
        System.out.println("jwtHeader : " + jwtHeader);

        // 헤더가 제대로 날라옴
        // jwt 토큰을 검증해서 정상적인 사용자인지 확인함
        if (jwtHeader == null || !jwtHeader.startsWith(JwtData.BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 토큰을 검증을 해서 정상적인 사용자인지 확인함
        try {
            String token = jwtHeader.replace(JwtData.BEARER, "");
            String username = jwtTokenProvider.validateToken(token);

            if (username != null) {
                User userEntity = userRepository.findByUsername(username);

                if (userEntity != null) {
                    PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            principalDetails, null, principalDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("[JWT filter] 인증 성공 : " + username);
                }
            }
        } catch (JWTVerificationException e) {
            // 잘못된 토큰 → 인증 정보 비우고 계속 진행
            // permitAll 경로는 통과, 보호 경로는 Spring Security가 403 처리
            SecurityContextHolder.clearContext();
            System.out.println("[JWT filter] 유효하지 않은 토큰 : " + e.getMessage());
        }
        // 항상 다음 필터로 진행 (인증 성공 여부와 무관)
        filterChain.doFilter(request, response);
    }

}
