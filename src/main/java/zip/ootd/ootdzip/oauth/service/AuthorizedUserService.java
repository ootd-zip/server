package zip.ootd.ootdzip.oauth.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.oauth.data.AuthorizedUser;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthorizedUserService {

    private final UserRepository userRepository;
    private final UserSocialLoginService userSocialLoginService;

    @Transactional
    public AuthorizedUser getAuthorizedUser(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String oauth2UserName = oauth2User.getName();

        Optional<User> foundUser = userSocialLoginService.findUser(registrationId, oauth2UserName);
        User user = foundUser.orElseGet(() -> {
            // 신규 유저 생성
            User newUser = User.getDefault();
            newUser = userRepository.save(newUser);
            userSocialLoginService.addUserSocialLogin(registrationId, oauth2UserName, newUser);
            return newUser;
        });

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.addAll(oauth2User.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(user.getUserRole().toString()));

        return new AuthorizedUser(authorities, oauth2User.getAttributes(), user);
    }
}
