package zip.ootd.ootdzip.oauth;

import java.time.Instant;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import zip.ootd.ootdzip.user.domain.User;

public class OAuthUtils {

    public static Authentication createJwtAuthentication(User user) {
        Map<String, Object> headers = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> claims = Map.of(
                JwtClaimNames.ISS, "ootdzip",
                JwtClaimNames.AUD, "ootdzip",
                JwtClaimNames.SUB, String.valueOf(user.getId()),
                JwtClaimNames.IAT, Instant.MIN,
                JwtClaimNames.EXP, Instant.MAX
        );
        Jwt jwt = new Jwt(".", Instant.MIN, Instant.MAX, headers, claims);
        return new JwtAuthenticationToken(jwt);
    }
}
