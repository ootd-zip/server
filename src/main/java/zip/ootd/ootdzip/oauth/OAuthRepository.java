package zip.ootd.ootdzip.oauth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthRepository extends JpaRepository<UserOAuth, Long> {

    UserOAuth findByOauthProviderAndOauthUserId(OAuthProvider oAuthProvider, String oAuthUserId);
}
