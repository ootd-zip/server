package zip.ootd.ootdzip.security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import zip.ootd.ootdzip.oauth.data.TokenInfo;
import zip.ootd.ootdzip.oauth.domain.UserAuthenticationToken;

@Component
public class JwtUtils {

    private final String issuer = "ootd.zip";
    private final SecretKey secretKey;
    private final SignatureAlgorithm signatureAlgorithm;
    private final long accessTokenLifetime;
    private final long refreshTokenLifetime;
    private final JwtParser jwtParser;

    public JwtUtils(@Value("${spring.security.jwt.secret-key}") String secretKey,
            @Value("${spring.security.jwt.signature-algorithm}") String signatureAlgorithm,
            @Value("${spring.security.jwt.expires-in:10800000}") long accessTokenLifetime, // 3 hours
            @Value("${spring.security.jwt.refresh-token-expires-in:604800000}") long refreshTokenLifetime // 7 days
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.signatureAlgorithm = SignatureAlgorithm.forName(signatureAlgorithm);
        this.accessTokenLifetime = accessTokenLifetime;
        this.refreshTokenLifetime = refreshTokenLifetime;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.secretKey).build();
    }

    public TokenInfo buildTokenInfo(Authentication authentication) {
        Date now = new Date();
        String accessToken = encode(authentication, now, accessTokenLifetime);
        String refreshToken = encode(authentication, now, refreshTokenLifetime);

        return TokenInfo.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .expiresIn((int)(accessTokenLifetime / 1000))
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn((int)(refreshTokenLifetime / 1000))
                .build();
    }

    public String encode(Authentication authentication, Date issuedAt, long lifetimeMs) {
        if (lifetimeMs < 0) {
            throw new IllegalArgumentException("Token lifetime must be positive but received: " + lifetimeMs);
        }
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(authentication.getName())
                .setIssuedAt(issuedAt)
                .setExpiration(new Date(issuedAt.getTime() + lifetimeMs))
                .signWith(secretKey, signatureAlgorithm)
                .compact();
    }

    public Authentication decode(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            UserAuthenticationToken.UserDetails principal = new UserAuthenticationToken.UserDetails(
                    Long.parseLong(claims.getSubject()));
            return new UserAuthenticationToken(principal);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | ExpiredJwtException
                 | IllegalArgumentException e) {
            return null;
        }
    }

    public void validate(String token) throws
            UnsupportedJwtException,
            MalformedJwtException,
            SignatureException,
            ExpiredJwtException,
            IllegalArgumentException {
        jwtParser.parseClaimsJws(token);
    }

}
