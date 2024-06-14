package zip.ootd.ootdzip.user.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import zip.ootd.ootdzip.user.data.UserSearchType;
import zip.ootd.ootdzip.user.domain.User;

public interface UserRepositoryCustom {

    Page<User> searchUsers(
            UserSearchType searchType,
            String name,
            Long userId,
            Set<Long> nonAccessibleUserIds,
            Pageable pageable);
}
