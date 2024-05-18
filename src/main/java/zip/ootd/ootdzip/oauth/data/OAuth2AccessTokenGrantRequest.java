package zip.ootd.ootdzip.oauth.data;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2AccessTokenGrantRequest {

    private ClientRegistration clientRegistration;
    private String code;
    private String redirectUri;

    public static OAuth2AccessTokenGrantRequest from(@NotNull ClientRegistration clientRegistration,
            @NotNull String code, @NotNull String redirectUri) {
        return new OAuth2AccessTokenGrantRequest(clientRegistration, code, redirectUri);
    }

    public RequestEntity<MultiValueMap<String, String>> toRequestEntity() {
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();
        HttpHeaders headers = buildHeaders();
        MultiValueMap<String, String> body = buildBody();
        return RequestEntity.post(tokenUri)
                .headers(headers)
                .body(body);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        MediaType formUrlencodedUtf8 = new MediaType(
                MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8);
        headers.setContentType(formUrlencodedUtf8);
        return headers;
    }

    private MultiValueMap<String, String> buildBody() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        String authorizationCodeGrantType = AuthorizationGrantType.AUTHORIZATION_CODE.getValue();
        String clientId = clientRegistration.getClientId();
        String clientSecret = clientRegistration.getClientSecret();

        requestBody.add(OAuth2ParameterNames.GRANT_TYPE, authorizationCodeGrantType);
        requestBody.add(OAuth2ParameterNames.CLIENT_ID, clientId);
        if (clientSecret != null) {
            requestBody.add(OAuth2ParameterNames.CLIENT_SECRET, clientSecret);
        }
        requestBody.add(OAuth2ParameterNames.CODE, code);
        requestBody.add(OAuth2ParameterNames.REDIRECT_URI, redirectUri);

        return requestBody;
    }

}

