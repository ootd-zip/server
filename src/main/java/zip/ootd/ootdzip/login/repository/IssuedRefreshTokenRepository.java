package zip.ootd.ootdzip.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import zip.ootd.ootdzip.login.domain.IssuedRefreshToken;

public interface IssuedRefreshTokenRepository extends JpaRepository<IssuedRefreshToken, Long> {

    Optional<IssuedRefreshToken> findByTokenValue(String tokenValue);
}
