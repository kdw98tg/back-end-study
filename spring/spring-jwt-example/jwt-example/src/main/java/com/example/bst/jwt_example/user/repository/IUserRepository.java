package com.example.bst.jwt_example.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bst.jwt_example.user.entity.User;

public interface IUserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String _username);
}
