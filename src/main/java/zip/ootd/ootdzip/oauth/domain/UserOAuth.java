package zip.ootd.ootdzip.oauth.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "user_oauths", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"oauth_provider", "oauth_provider_id"}),
        @UniqueConstraint(columnNames = {"user_id", "oauth_provider"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserOAuth extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "oauth_provider", nullable = false)
    private OAuthProvider oauthProvider;

    @Column(name = "oauth_provider_id", nullable = false)
    private String oauthUserId;
}
