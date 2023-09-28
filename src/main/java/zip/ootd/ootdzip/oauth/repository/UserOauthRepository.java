package zip.ootd.ootdzip.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import zip.ootd.ootdzip.oauth.domain.OauthProvider;
import zip.ootd.ootdzip.oauth.domain.UserOauth;

public interface UserOauthRepository extends JpaRepository<UserOauth, Long> {
    Optional<UserOauth> findUserOauthByOauthProviderAndOauthUserId(OauthProvider oauthProvider, String oauthUserId);
}
