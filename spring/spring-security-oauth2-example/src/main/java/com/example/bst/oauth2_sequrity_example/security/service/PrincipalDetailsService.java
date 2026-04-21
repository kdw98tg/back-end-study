package com.example.bst.oauth2_sequrity_example.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bst.oauth2_sequrity_example.security.entity.PrincipalDetails;
import com.example.bst.oauth2_sequrity_example.user.entity.User;
import com.example.bst.oauth2_sequrity_example.user.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

//spring security에서 로그인을 시도할 때,
//자동으로 /loginProc 이라는 라우팅이 호출되고
//거기서 작업을 할때, 자동으로 UserDetailsService를 상속받는 @Serivce 로 Bean으로 등록된 객체의
//loadUserByUsername 함수를 호출하게 됨. 
//여기서 로그인을 처리해주면 됨

// 그렇게 로그인이 완료되면 
// 자동으로 sequrity session을 만들어줌
// Security ContextHolder에 값을 저장함
// Security Session 안에 Authentication 이라는 객체가 있고 그 안에 UserDetails 라는 객체가 있음

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PrincipalDetails result = null;

        User user = userRepository.findByUsername(username);
        if (user != null) {
            result = new PrincipalDetails(user);
        }

        return result;
    }

}
