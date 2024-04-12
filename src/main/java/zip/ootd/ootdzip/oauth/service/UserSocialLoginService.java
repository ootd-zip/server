package zip.ootd.ootdzip.oauth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.oauth.domain.UserSocialLogin;
import zip.ootd.ootdzip.oauth.repository.UserSocialLoginRepository;
import zip.ootd.ootdzip.user.domain.User;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserSocialLoginService {

    private final UserSocialLoginRepository userSocialLoginRepository;

    public Optional<User> findUser(String provider, String providerId) {
        return userSocialLoginRepository.findByProviderAndProviderId(provider, providerId)
                .map(UserSocialLogin::getUser);
    }

    @Transactional
    public void addUserSocialLogin(String provider, String providerId, User user) {
        if (userSocialLoginRepository.findByProviderAndProviderId(provider, providerId).isPresent()) {
            throw new RuntimeException(String.format("추가하려는 소셜 로그인 정보가 이미 존재합니다. %s: %s", provider, providerId));
        }

        UserSocialLogin socialLogin = UserSocialLogin.builder()
                .user(user)
                .provider(provider)
                .providerId(providerId)
                .build();
        userSocialLoginRepository.save(socialLogin);
    }
}
