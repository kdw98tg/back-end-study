package com.example.bst.jwt_example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import com.example.bst.jwt_example.jwt.JwtAuthenticationFilter;
import com.example.bst.jwt_example.jwt.JwtAuthorizationFilter;
import com.example.bst.jwt_example.jwt.JwtTokenProvider;
import com.example.bst.jwt_example.user.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsFilter corsFilter;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final IUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity _http) throws Exception {
        //TODO 이거 왜 여기 있어야할까
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();


        _http.csrf(csrf -> csrf.disable())
                .addFilter(corsFilter)
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/v1/join").permitAll()
                                                .requestMatchers("/api/v1/user/**")
                                                .hasAnyRole("USER", "MANAGER", "ADMIN")
                                                .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                                .anyRequest().permitAll())


                .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider))
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository, jwtTokenProvider));
                                        

        return _http.build();
    }
}
