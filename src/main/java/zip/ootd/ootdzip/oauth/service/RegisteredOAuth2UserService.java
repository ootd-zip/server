package zip.ootd.ootdzip.oauth.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.oauth.data.RegisteredOAuth2User;
import zip.ootd.ootdzip.oauth.domain.UserSocialLogin;
import zip.ootd.ootdzip.oauth.repository.UserSocialLoginRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RegisteredOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    private final UserRepository userRepository;
    private final UserSocialLoginRepository userSocialLoginRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 토큰 정보 API에서 사용자 정보 받아오기
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        // 받아온 정보의 sub로 필요 시 DB에 저장하고 user 가져오기
        User user = findOrRegister(userRequest, oauth2User);
        // name이 user.id로 설정된 OAuth2User 반환
        return RegisteredOAuth2User.from(oauth2User, user);
    }

    private User findOrRegister(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oauth2User.getName();

        return find(provider, providerId).orElseGet(() -> register(provider, providerId));
    }

    private Optional<User> find(String provider, String providerId) {
        return userSocialLoginRepository
                .findByProviderAndProviderId(provider, providerId)
                .map(UserSocialLogin::getUser);
    }

    private User register(String provider, String providerId) {
        User user = User.getDefault();
        user = userRepository.save(user);

        UserSocialLogin socialLogin = UserSocialLogin.builder()
                .user(user)
                .provider(provider)
                .providerId(providerId)
                .build();
        socialLogin = userSocialLoginRepository.save(socialLogin);

        return user;
    }
}
