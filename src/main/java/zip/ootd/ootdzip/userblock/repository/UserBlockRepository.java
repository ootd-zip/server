package zip.ootd.ootdzip.userblock.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.userblock.domain.UserBlock;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    List<UserBlock> findAllByBlockUser(User BlockUser, Pageable pageable);

    Boolean existsByBlockedUserAndBlockUser(User BlockedUser, User BlockUser);
}
