package zip.ootd.ootdzip.userblock.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.userblock.domain.UserBlock;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long>, UserBlockRepositoryCustom {

    @Query("SELECT ub FROM UserBlock ub "
            + "WHERE ub.blockUser = :blockUser "
            + "AND ub.blockUser.isDeleted = false ")
    Slice<UserBlock> findAllByBlockUser(@Param("blockUser") User blockUser, Pageable pageable);

    Boolean existsByBlockedUserAndBlockUser(User blockedUser, User blockUser);
}
