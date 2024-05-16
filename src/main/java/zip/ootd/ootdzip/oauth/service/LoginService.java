package zip.ootd.ootdzip.oauth.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.oauth.data.AuthorizedUser;
import zip.ootd.ootdzip.oauth.data.TokenResponse;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthorizedOAuth2UserService authorizedOAuth2UserService;
    private final TokenService tokenService;

    public TokenResponse token(@NotNull String registrationId, @NotNull String authorizationCode) {
        ClientRegistration clientRegistration = getClientRegistration(registrationId);
        OAuth2AccessToken accessToken = exchangeAccessToken(clientRegistration, authorizationCode);
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

    private OAuth2AccessToken exchangeAccessToken(ClientRegistration clientRegistration, String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(OAuth2ParameterNames.GRANT_TYPE, "authorization_code");
        body.add(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId());
        body.add(OAuth2ParameterNames.REDIRECT_URI, clientRegistration.getRedirectUri());
        body.add(OAuth2ParameterNames.CODE, code);
        if (clientRegistration.getClientSecret() != null) {
            body.add(OAuth2ParameterNames.CLIENT_SECRET, clientRegistration.getClientSecret());
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request,
                new ParameterizedTypeReference<>() {
                });

        if (response.getStatusCode().isError()) {
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_ERROR);
        }

        Map<String, Object> tokenResponse = response.getBody();
        String accessToken = (String)tokenResponse.get(OAuth2ParameterNames.ACCESS_TOKEN);
        Integer expiresIn = (Integer)tokenResponse.get(OAuth2ParameterNames.EXPIRES_IN);
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(expiresIn);
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, issuedAt, expiresAt);
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
