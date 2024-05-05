package zip.ootd.ootdzip.oauth.service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.oauth.data.AuthorizedUser;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthorizedOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String ID_TOKEN = "id_token";

    private final UserRepository userRepository;
    private final UserSocialLoginService userSocialLoginService;

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        User serviceUser;
        Map<String, Object> attributes;
        Collection<? extends GrantedAuthority> authorities;

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if ("apple".equals(registrationId)) {
            // Apple ID일 경우 id_token에서 sub 추출
            String idToken = userRequest.getAdditionalParameters().get(ID_TOKEN).toString();
            Map<String, Object> claims = parseClaims(idToken);
            String providerUserId = (String)claims.get(JwtClaimNames.SUB);

            serviceUser = findOrCreateUser(registrationId, providerUserId);
            attributes = Map.of(JwtClaimNames.SUB, providerUserId);
            authorities = Set.of(new OAuth2UserAuthority(attributes));
        } else {
            // 토큰 정보 API에서 사용자 정보 받아오기
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            // 받아온 정보의 sub로 필요 시 DB에 저장하고 user 가져오기
            String providerUserId;
            if ("naver".equals(registrationId)) {
                // Spring Security 6.3.0-M2부터 nested user-name-attribute 설정 가능
                // https://github.com/spring-projects/spring-security/pull/14265
                providerUserId = (String)((Map<String, Object>)oauth2User.getAttributes().get("response")).get("id");
            } else {
                providerUserId = oauth2User.getName();
            }
            serviceUser = findOrCreateUser(registrationId, providerUserId);
            attributes = oauth2User.getAttributes();
            authorities = oauth2User.getAuthorities();
        }

        // 탈퇴한 유저가 소셜 로그인을 시도할 경우 에러
        if (serviceUser.getIsDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER_ERROR);
        }

        return new AuthorizedUser(authorities, attributes, serviceUser);
    }

    private User findOrCreateUser(String provider, String providerId) {
        Optional<User> optionalUser = userSocialLoginService.findUser(provider, providerId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        User user = User.getDefault();
        user = userRepository.save(user);
        userSocialLoginService.addUserSocialLogin(provider, providerId, user);

        return user;
    }

    private Map<String, Object> parseClaims(String jwt) {
        // JwtParser를 사용하면 alg, exp 검사 과정이 포함되기 때문에 단순히 payload만 변환
        Charset chs = StandardCharsets.UTF_8;
        String[] parts = jwt.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1].getBytes(chs)), chs);
        try {
            return new ObjectMapper().readValue(payload, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
