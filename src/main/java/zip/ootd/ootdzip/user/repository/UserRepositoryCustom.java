package zip.ootd.ootdzip.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import zip.ootd.ootdzip.user.domain.User;

public interface UserRepositoryCustom {
    Slice<User> searchUsers(String name, Pageable pageable);

    Slice<User> searchFollowers(String name, User loginUser, Pageable pageable);

    Slice<User> searchFollowings(String name, User loginUser, Pageable pageable);
}
