package zip.ootd.ootdzip.oauth.service;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.oauth.data.AuthorizedUser;
import zip.ootd.ootdzip.oauth.data.OAuth2AccessTokenGrantRequest;
import zip.ootd.ootdzip.oauth.data.OAuth2AccessTokenGrantResponse;
import zip.ootd.ootdzip.oauth.data.TokenResponse;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthorizedOAuth2UserService authorizedOAuth2UserService;
    private final TokenService tokenService;

    public TokenResponse token(@NotNull String registrationId, @NotNull String authorizationCode,
            @NotNull String redirectUri) {
        ClientRegistration clientRegistration = getClientRegistration(registrationId);
        OAuth2AccessToken accessToken = exchangeAccessToken(clientRegistration, authorizationCode, redirectUri);
        AuthorizedUser authorizedUser = loadAuthorizedUser(clientRegistration, accessToken);
        return issueNewAccessToken(authorizedUser);
    }

    private ClientRegistration getClientRegistration(String registrationId) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new CustomException(ErrorCode.NONE_SOCIAL_ERROR);
        }
        return clientRegistration;
    }

    private OAuth2AccessToken exchangeAccessToken(ClientRegistration clientRegistration, String code,
            String redirectUri) {
        OAuth2AccessTokenGrantRequest request = OAuth2AccessTokenGrantRequest.from(clientRegistration, code,
                redirectUri);

        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<MultiValueMap<String, String>> requestEntity = request.toRequestEntity();

        ResponseEntity<OAuth2AccessTokenGrantResponse> response = restTemplate.exchange(requestEntity,
                OAuth2AccessTokenGrantResponse.class);

        if (response.getStatusCode().isError()) {
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_ERROR);
        }

        return response.getBody().getOAuth2AccessToken();
    }

    private AuthorizedUser loadAuthorizedUser(ClientRegistration clientRegistration, OAuth2AccessToken accessToken) {
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessToken);
        return (AuthorizedUser)authorizedOAuth2UserService.loadUser(userRequest);
    }

    private TokenResponse issueNewAccessToken(AuthorizedUser authorizedUser) {
        User user = authorizedUser.getUser();
        return tokenService.issueNewAccessToken(user);
    }
}
