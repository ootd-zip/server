package zip.ootd.ootdzip.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import zip.ootd.ootdzip.user.domain.User;

public interface UserRepositoryCustom {
    Page<User> searchUsers(String name, Pageable pageable);

    Page<User> searchFollowers(String name, Long userId, Pageable pageable);

    Page<User> searchFollowings(String name, Long userId, Pageable pageable);
}
