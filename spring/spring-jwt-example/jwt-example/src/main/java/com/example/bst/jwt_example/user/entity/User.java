package com.example.bst.jwt_example.user.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String roles;

    public List<String> getRoleList() {
        if (this.roles != null && !this.roles.isEmpty()) {
            return Arrays.asList(this.roles.split(","));
        }

        return new ArrayList<>();
    }
}
