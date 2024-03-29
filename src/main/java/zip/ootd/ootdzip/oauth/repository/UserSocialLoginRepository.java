package zip.ootd.ootdzip.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import zip.ootd.ootdzip.oauth.domain.UserSocialLogin;

public interface UserSocialLoginRepository extends JpaRepository<UserSocialLogin, Long> {

    Optional<UserSocialLogin> findByProviderAndProviderId(String provider, String providerId);
}
