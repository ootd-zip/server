package zip.ootd.ootdzip.oauth.token;

import java.time.Instant;
import java.util.Collections;

import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccessTokenGenerator implements TokenGenerator<Jwt> {

    private final JwsHeader jwsHeader;
    private final JwtEncoder jwtEncoder;

    @Override
    public Jwt generate(TokenParams params) {
        if (!TokenType.ACCESS_TOKEN.equals(params.getTokenType())) {
            return null;
        }

        String issuer = params.getIssuer();

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(params.getTokenTimeToLive());

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        if (StringUtils.hasText(issuer)) {
            claimsBuilder.issuer(issuer);
        }
        claimsBuilder
                .subject(params.getUserId())
                .audience(Collections.singletonList(params.getAudience()))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .notBefore(issuedAt);

        JwtClaimsSet claims = claimsBuilder.build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims));
    }
}
