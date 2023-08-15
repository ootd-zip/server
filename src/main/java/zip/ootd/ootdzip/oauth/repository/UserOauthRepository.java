package zip.ootd.ootdzip.oauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zip.ootd.ootdzip.oauth.domain.OauthProvider;
import zip.ootd.ootdzip.oauth.domain.UserOauth;

import java.util.Optional;

public interface UserOauthRepository extends JpaRepository<UserOauth, Long> {
    Optional<UserOauth> findUserOauthByOauthProviderAndOauthUserId(OauthProvider oauthProvider, String oauthUserId);
}
