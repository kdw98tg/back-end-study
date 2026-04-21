package com.example.bst.jwt_example.jwt.filter.authentication;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.bst.jwt_example.auth.model.PrincipalDetails;
import com.example.bst.jwt_example.jwt.data.JwtData;
import com.example.bst.jwt_example.jwt.provider.JwtTokenProvider;
import com.example.bst.jwt_example.user.entity.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//스프링 시큐리티에서는 UsernamePasswordAuthenticationFilter 라는 필터가 있다.
//이 필터는 로그인 요청을 가로채서, 로그인 시도를 하고, 성공하면 JWT 토큰을 만들어서 응답해주는 역할을 한다.
//따라서, 우리는 이 필터를 상속받아서, 우리가 원하는 방식으로 JWT 토큰을 만들어서 응답해주는 필터를 만들어야 한다.
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager _authenticationManager, JwtTokenProvider _jwtTokenProvider) {
        this.authenticationManager = _authenticationManager;
        this.jwtTokenProvider = _jwtTokenProvider;
        setAuthenticationManager(authenticationManager);
    }

    // 로그인 요청을 하면, 로그인 시도를 위해서 실행되는 함수임
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        System.out.println("JwtAuthenticationFilter : 로그인 시도중");

        // 1. username, password를 받아서 정상적인 로그인 시도를 해봄
        // authenticationManager로 로그인 시도를 하면, PrincipalDetailsService의
        // loadUserByUsername() 함수가 실행됨
        // 2. PrincipalDetailsService의 loadUserByUsername() 함수가 끝나면, 정상이면 authentication
        // 객체가 만들어짐
        // 3. spring security의 세션에 authentication 객체를 저장함 -> 로그인이 되었다는 뜻
        // 4. JWT 토큰을 만들어서 응답해주면 됨 (JWT 토큰은 Authorization 헤더에 담아서 응답)

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Authentication result = null;

        try {
            System.out.println(request.getInputStream());
            User user = objectMapper.readValue(request.getInputStream(), User.class);
            System.out.println(user);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword());

            // authenticationManager에 있는 유저의 이름과 패스워드가
            // 인증 요청으로 들어온 유저의 이름과 패스워드가 일치하는지 확인함
            result = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) result.getPrincipal();

            // 값이 있으면 정상적으로 로그인 됐다는 뜻임
            System.out.println("로그인 완료됨 : " + principalDetails.getUser().getUsername());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    // attemptAuthentication 실행 후, 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨
    // 사용자 신원이 확인되었으므로, jwt 토큰을 만들어서 response 로 돌려주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨 : 인증이 완료되었다는 뜻임");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = jwtTokenProvider.generateToken(principalDetails.getUsername(),
                principalDetails.getUser().getRoles());

        response.addHeader("Authorization", JwtData.BEARER + jwtToken);
    }
}
