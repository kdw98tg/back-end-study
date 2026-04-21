package com.example.bst.jwt_example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import com.example.bst.jwt_example.jwt.filter.authentication.JwtAuthenticationFilter;
import com.example.bst.jwt_example.jwt.filter.authorization.JwtAuthorizationFilter;
import com.example.bst.jwt_example.jwt.provider.JwtTokenProvider;
import com.example.bst.jwt_example.user.repository.IUserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity _http,
            CorsFilter corsFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthorizationFilter jwtAuthorizationFilter) throws Exception {


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


                .addFilter(jwtAuthenticationFilter)
                .addFilter(jwtAuthorizationFilter);
                                        

        return _http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider) {
        return new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
            AuthenticationManager authenticationManager,
            IUserRepository userRepository,
            JwtTokenProvider jwtTokenProvider) {
        return new JwtAuthorizationFilter(authenticationManager, userRepository, jwtTokenProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
