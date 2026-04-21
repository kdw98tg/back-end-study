package com.example.bst.oauth2_sequrity_example.security.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.bst.oauth2_sequrity_example.user.entity.User;

public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user = null;

    private Map<String, Object> attirbutes = null;

    public PrincipalDetails(User _user) {
        this.user = _user;
    }

    public PrincipalDetails(User _user, Map<String, Object> _attributes){
        this.user = _user;
        this.attirbutes = _attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> result = new ArrayList<>();

        result.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return result;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attirbutes;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }
}
