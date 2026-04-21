package com.example.bst.jwt_example.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bst.jwt_example.user.entity.User;
import com.example.bst.jwt_example.user.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void join(User _user) {
        _user.setPassword(passwordEncoder.encode(_user.getPassword()));
        _user.setRoles("ROLE_USER");
        userRepository.save(_user);
    }
}
