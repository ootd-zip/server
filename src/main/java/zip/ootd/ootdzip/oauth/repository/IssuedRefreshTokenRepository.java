package zip.ootd.ootdzip.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import zip.ootd.ootdzip.oauth.domain.IssuedRefreshToken;

public interface IssuedRefreshTokenRepository extends JpaRepository<IssuedRefreshToken, Long> {

    Optional<IssuedRefreshToken> findByTokenValue(String tokenValue);
}
