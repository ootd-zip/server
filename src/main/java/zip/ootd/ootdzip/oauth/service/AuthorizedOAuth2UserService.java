package zip.ootd.ootdzip.oauth.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthorizedOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String ATTRIBUTE_DELIMITER = "/";

    private final AuthorizedUserService authorizedUserService;

    private final CustomOAuth2UserService delegate;

    public AuthorizedOAuth2UserService(AuthorizedUserService authorizedUserService) {
        this.authorizedUserService = authorizedUserService;
        this.delegate = new CustomOAuth2UserService();

        this.delegate.setAttributesConverter((request) -> (attributes) -> {
            String userNameAttributeName = getUserNameAttributeName(request);
            // naver 로그인 시 "response" -> "id" 경로에 저장된 사용자 id를 "response/id"를 키로 저장
            if (userNameAttributeName.contains(ATTRIBUTE_DELIMITER)) {
                String[] keys = userNameAttributeName.split(ATTRIBUTE_DELIMITER);
                String userName = getNestedValue(attributes, keys);
                attributes.put(userNameAttributeName, userName);
            }
            return attributes;
        });
    }

    private static <T> T getNestedValue(Map<String, Object> map, String[] keys) {
        Map<String, Object> cur = map;
        for (String key : keys) {
            Object o = cur.get(key);
            if (o == null) {
                return null;
            }
            if (o instanceof Map) {
                cur = (Map<String, Object>)o;
            } else {
                return (T)o;
            }
        }
        return null;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        return authorizedUserService.getAuthorizedUser(userRequest, oauth2User);
    }

    private String getUserNameAttributeName(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
    }
}
