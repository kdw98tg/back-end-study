package com.example.bst.oauth2_sequrity_example.oauth2.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.bst.oauth2_sequrity_example.oauth2.interfaces.IOAuth2UserInfo;
import com.example.bst.oauth2_sequrity_example.oauth2.providers.facebook.FacebookUserInfo;
import com.example.bst.oauth2_sequrity_example.oauth2.providers.google.GoogleUserInfo;
import com.example.bst.oauth2_sequrity_example.oauth2.providers.naver.NaverUserInfo;
import com.example.bst.oauth2_sequrity_example.security.entity.PrincipalDetails;
import com.example.bst.oauth2_sequrity_example.user.entity.User;
import com.example.bst.oauth2_sequrity_example.user.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final IUserRepository userRepository;

    // oauth2 버튼 누르면, 이쪽으로 리다이렉트 해서 oauth2 서버에서 정보를 제공하면 해당 클래스의 loadUser 를 호출함
    // userRequest는 요청한 정보에대한 정보
    // 즉, 유저 정보임
    // 함수 종료시, @AuthenticationPrincipal 어노테이션이 만들어짐
    // 이제 controller에서 @AuthenticationPrincipal 어노테이션을 가지고 인증된 유저를 가져올 수 있음
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println(userRequest);

        // 클라이언트의 아이디 (google, naver, facebook 등)
        System.out.println(userRequest.getClientRegistration().getClientId());
        System.out.println(userRequest.getAccessToken());
        System.out.println(userRequest.getAccessToken().getTokenValue());

        // 이게 oauth2 서버가 제공하는 사용자의 정보
        System.out.println(super.loadUser(userRequest).getAttributes());

        OAuth2User oauth2User = super.loadUser(userRequest);

        IOAuth2UserInfo userInfo = getOauth2UserInfoByClientName(userRequest, oauth2User);

        User userEntity = joinOrGetExistUser(userInfo);

        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }

    private User joinOrGetExistUser(IOAuth2UserInfo _userInfo) {
        User result = null;

        String provider = _userInfo.getProvider();
        String providerId = _userInfo.getProviderId();
        String username = provider + "_" + providerId;// google_1030203014002 이런식으로 저장됨
        // String password = bCryptPasswordEncoder.encode("겟인데어"); // 안에 들어가는 스트링은 auth
        // 시 상관없음
        String email = _userInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            userEntity = User.builder().username(username).password("").email(email).role(role)
                    .providerId(providerId).provider(provider).build();

            result = userRepository.save(userEntity);
        }

        return result;
    }

    private IOAuth2UserInfo getOauth2UserInfoByClientName(OAuth2UserRequest _userRequest, OAuth2User _oauth2User) {
        IOAuth2UserInfo result = null;

        String clientName = _userRequest.getClientRegistration().getRegistrationId();

        switch (clientName) {
            case "google":
                result = new GoogleUserInfo(_oauth2User.getAttributes());
                break;
            case "facebook":
                result = new FacebookUserInfo(_oauth2User.getAttributes());
                break;
            case "naver":
                result = new NaverUserInfo(_oauth2User.getAttributes());
                break;
        }

        return result;
    }

}