package zip.ootd.ootdzip.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserStyle;

@Repository
public interface UserStyleRepository extends JpaRepository<UserStyle, Long> {

    List<UserStyle> findAllByUser(User user);
}
