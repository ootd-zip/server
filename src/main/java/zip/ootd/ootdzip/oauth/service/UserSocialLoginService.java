package zip.ootd.ootdzip.oauth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.oauth.domain.UserSocialLogin;
import zip.ootd.ootdzip.oauth.repository.UserSocialLoginRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserSocialLoginService {

    private final UserRepository userRepository;
    private final UserSocialLoginRepository userSocialLoginRepository;

    @Transactional
    public User findOrRegister(String provider, String providerId) {
        return find(provider, providerId).orElseGet(() -> register(provider, providerId));
    }

    @Transactional(readOnly = true)
    public Optional<User> find(String provider, String providerId) {
        return userSocialLoginRepository.findByProviderAndProviderId(provider, providerId)
                .map(UserSocialLogin::getUser);
    }

    @Transactional
    public User register(String provider, String providerId) {
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
