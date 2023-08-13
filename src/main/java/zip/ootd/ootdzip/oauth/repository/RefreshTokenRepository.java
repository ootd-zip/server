package zip.ootd.ootdzip.oauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zip.ootd.ootdzip.oauth.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
