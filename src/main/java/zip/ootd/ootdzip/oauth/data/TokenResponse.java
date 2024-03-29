package zip.ootd.ootdzip.oauth.data;

import java.time.Instant;

import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenResponse {

    private String tokenType;
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
    private Long refreshTokenExpiresIn;

    public static TokenResponse of(AbstractOAuth2Token accessToken, AbstractOAuth2Token refreshToken) {
        long nowEpochSecond = Instant.now().getEpochSecond();
        return new TokenResponse(
                OAuth2AccessToken.TokenType.BEARER.getValue(),
                accessToken.getTokenValue(),
                accessToken.getExpiresAt().getEpochSecond() - nowEpochSecond - 1,
                refreshToken.getTokenValue(),
                refreshToken.getExpiresAt().getEpochSecond() - nowEpochSecond
        );
    }
}
