package zip.ootd.ootdzip.oauth.provider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

@Component
public class AppleClientRegistration {

    private static final String REGISTRATION_ID = "apple";
    private static final String SERVER_URL = "https://appleid.apple.com";
    private static AppleClientRegistration instance;
    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.apple.team-id}")
    private String teamId;
    @Value("${spring.security.oauth2.client.registration.apple.key-id}")
    private String keyId;
    @Value("${spring.security.oauth2.client.registration.apple.key-path}")
    private String keyPath;
    private PrivateKey privateKey;

    public static String getRegistrationId() {
        return REGISTRATION_ID;
    }

    public static String getClientSecret() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(Duration.ofMinutes(5));

        // @formatter:off
        return Jwts.builder()
                .header().empty().keyId(instance.keyId).and()
                .issuer(instance.teamId)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .audience().add(SERVER_URL).and()
                .subject(instance.clientId)
                .signWith(instance.privateKey)
                .compact();
        // @formatter:on
    }

    private static PrivateKey createPrivateKey(String keyPath) {
        try {
            ClassPathResource keyFile = new ClassPathResource(keyPath);
            byte[] keyBytes = keyFile.getInputStream().readAllBytes();
            String keyString = new String(keyBytes, StandardCharsets.UTF_8).replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .trim();
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyString));
            return KeyFactory.getInstance("EC").generatePrivate(keySpec);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    private void initialize() {
        this.privateKey = createPrivateKey(keyPath);

        if (instance == null) {
            instance = this;
        }
    }
}
