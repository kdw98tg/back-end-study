package com.example.bst.oauth2_sequrity_example.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bst.oauth2_sequrity_example.user.entity.User;

public interface IUserRepository extends JpaRepository<User, Integer> {

    public User findByUsername(String username);
    
}
