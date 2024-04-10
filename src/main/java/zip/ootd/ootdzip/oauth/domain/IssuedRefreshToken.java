package zip.ootd.ootdzip.oauth.domain;

import java.time.Instant;

import org.springframework.security.oauth2.core.AbstractOAuth2Token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "issued_refresh_tokens")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class IssuedRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_value", nullable = false)
    private String tokenValue;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long issuedAt;

    @Column(nullable = false)
    private Long expiresAt;

    @Column(nullable = false)
    private Boolean revoked;

    @Column(nullable = false)
    private Boolean invalidated;

    public static IssuedRefreshToken from(AbstractOAuth2Token token, User user) {
        return IssuedRefreshToken.builder()
                .tokenValue(token.getTokenValue())
                .user(user)
                .issuedAt(token.getIssuedAt().getEpochSecond())
                .expiresAt(token.getExpiresAt().getEpochSecond())
                .revoked(false)
                .invalidated(false)
                .build();
    }

    public boolean isRevokedOrInvalidated() {
        return revoked || invalidated;
    }

    public boolean isExpired(Instant now) {
        Instant iat = Instant.ofEpochSecond(issuedAt);
        Instant exp = Instant.ofEpochSecond(expiresAt);
        return now.isBefore(iat) || now.isAfter(exp);
    }

    public void revoke() {
        if (revoked) {
            // TODO: 적절한 예외 발생
            throw new RuntimeException("Already revoked");
        }
        revoked = true;
    }

    public void invalidate() {
        if (invalidated) {
            // TODO: 적절한 예외 발생
            throw new RuntimeException("Already invalidated");
        }
        invalidated = true;
    }
}
