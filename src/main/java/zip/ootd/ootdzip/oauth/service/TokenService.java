package zip.ootd.ootdzip.oauth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.oauth.data.TokenResponse;
import zip.ootd.ootdzip.oauth.domain.IssuedRefreshToken;
import zip.ootd.ootdzip.oauth.repository.IssuedRefreshTokenRepository;
import zip.ootd.ootdzip.oauth.token.TokenGenerator;
import zip.ootd.ootdzip.oauth.token.TokenParams;
import zip.ootd.ootdzip.oauth.token.TokenType;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    @Value("${authorization.issuer}")
    private String issuer;
    @Value("${authorization.audience}")
    private String audience;
    @Value("${authorization.access-token-time-to-live}")
    private Long accessTokenTimeToLive;
    @Value("${authorization.refresh-token-time-to-live}")
    private Long refreshTokenTimeToLive;

    private final TokenGenerator<?> accessTokenGenerator;
    private final TokenGenerator<?> refreshTokenGenerator;
    private final IssuedRefreshTokenRepository issuedRefreshTokenRepository;

    @Transactional
    public TokenResponse issueNewAccessToken(User user) {
        return generateToken(user);
    }

    @Transactional
    public TokenResponse refreshAccessToken(String refreshTokenValue) {
        Optional<IssuedRefreshToken> optionalFoundRefreshToken = issuedRefreshTokenRepository
                .findByTokenValue(refreshTokenValue);
        if (optionalFoundRefreshToken.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN_ERROR);
        }
        IssuedRefreshToken foundRefreshToken = optionalFoundRefreshToken.get();
        if (foundRefreshToken.isRevokedOrInvalidated() || foundRefreshToken.isExpired(Instant.now())) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN_ERROR);
        }

        User user = foundRefreshToken.getUser();

        foundRefreshToken.invalidate();
        foundRefreshToken = issuedRefreshTokenRepository.save(foundRefreshToken);

        return generateToken(user);
    }

    @Transactional
    public void revokeRefreshToken(String refreshTokenValue) {
        Optional<IssuedRefreshToken> optionalFoundRefreshToken = issuedRefreshTokenRepository.findByTokenValue(
                refreshTokenValue);
        // Note: invalid tokens do not cause an error response since the client
        // cannot handle such an error in a reasonable way. Moreover, the
        // purpose of the revocation request, invalidating the particular token,
        // is already achieved.
        if (optionalFoundRefreshToken.isEmpty()) {
            return;
        }
        IssuedRefreshToken foundRefreshToken = optionalFoundRefreshToken.get();
        foundRefreshToken.revoke();
        foundRefreshToken = issuedRefreshTokenRepository.save(foundRefreshToken);
    }

    private TokenResponse generateToken(User user) {
        AbstractOAuth2Token accessToken = accessTokenGenerator.generate(accessTokenParams(user));
        AbstractOAuth2Token refreshToken = refreshTokenGenerator.generate(refreshTokenParams(user));

        IssuedRefreshToken issuedRefreshToken = IssuedRefreshToken.from(refreshToken, user);
        issuedRefreshToken = issuedRefreshTokenRepository.save(issuedRefreshToken);

        return TokenResponse.of(accessToken, refreshToken);
    }

    private TokenParams accessTokenParams(User user) {
        return TokenParams.builder()
                .tokenType(TokenType.ACCESS_TOKEN)
                .issuer(issuer)
                .audience(audience)
                .userId(String.valueOf(user.getId()))
                .tokenTimeToLive(Duration.ofSeconds(accessTokenTimeToLive))
                .build();
    }

    private TokenParams refreshTokenParams(User user) {
        return TokenParams.builder()
                .tokenType(TokenType.REFRESH_TOKEN)
                .issuer(issuer)
                .audience(audience)
                .userId(String.valueOf(user.getId()))
                .tokenTimeToLive(Duration.ofSeconds(refreshTokenTimeToLive))
                .build();
    }
}
