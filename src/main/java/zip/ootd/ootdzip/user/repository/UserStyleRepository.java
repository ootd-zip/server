package zip.ootd.ootdzip.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.user.domain.UserStyle;

@Repository
public interface UserStyleRepository extends JpaRepository<UserStyle, Long> {
}
