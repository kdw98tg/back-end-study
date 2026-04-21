package com.example.bst.oauth2_sequrity_example.oauth2.providers.facebook;

import java.util.Map;

import com.example.bst.oauth2_sequrity_example.oauth2.interfaces.IOAuth2UserInfo;

public class FacebookUserInfo implements IOAuth2UserInfo {

    private Map<String, Object> attributes; // getAttributes()

    public FacebookUserInfo(Map<String, Object> _attributes){
        attributes = _attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "facebook";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

}
