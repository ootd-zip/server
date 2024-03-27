package zip.ootd.ootdzip.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import zip.ootd.ootdzip.login.domain.UserSocialLogin;

public interface UserSocialLoginRepository extends JpaRepository<UserSocialLogin, Long> {

    Optional<UserSocialLogin> findByProviderAndProviderId(String provider, String providerId);
}
