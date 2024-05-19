package zip.ootd.ootdzip.oauth.data;

import java.time.Instant;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class OAuth2AccessTokenGrantResponse {

    @JsonProperty(OAuth2ParameterNames.TOKEN_TYPE)
    private String tokenType;

    @JsonProperty(OAuth2ParameterNames.ACCESS_TOKEN)
    private String accessToken;

    @JsonProperty(OAuth2ParameterNames.EXPIRES_IN)
    private Integer expiresIn;

    public OAuth2AccessToken getOAuth2AccessToken() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(expiresIn);
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, issuedAt, expiresAt);
    }
}
