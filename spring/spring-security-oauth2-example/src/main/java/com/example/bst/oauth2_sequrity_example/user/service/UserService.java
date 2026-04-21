package com.example.bst.oauth2_sequrity_example.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bst.oauth2_sequrity_example.user.entity.User;
import com.example.bst.oauth2_sequrity_example.user.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(User _user) {
        System.out.println(_user);

        String rawPassword = _user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);

        _user.setRole("ROLE_USER");
        _user.setPassword(encPassword);
        
        userRepository.save(_user);// 이렇게 되면 시큐리티로 로그인할 수 없음 (패스워드가 암호화가 안되었기 때문에)
    }
}
