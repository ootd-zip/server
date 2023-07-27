package zip.ootd.ootdzip.oauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zip.ootd.ootdzip.oauth.domain.OAuthProvider;
import zip.ootd.ootdzip.oauth.domain.UserOAuth;

public interface UserOAuthRepository extends JpaRepository<UserOAuth, Long> {

    UserOAuth findByOauthProviderAndOauthUserId(OAuthProvider oAuthProvider, String oAuthUserId);
}
