package zip.ootd.ootdzip.user.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import zip.ootd.ootdzip.user.domain.User;

public interface UserRepositoryCustom {
    Page<User> searchUsers(String name, Set<Long> nonAccessibleUserIds, Pageable pageable);

    Page<User> searchFollowers(String name, Long userId, Set<Long> nonAccessibleUserIds, Pageable pageable);

    Page<User> searchFollowings(String name, Long userId, Set<Long> nonAccessibleUserIds, Pageable pageable);
}
