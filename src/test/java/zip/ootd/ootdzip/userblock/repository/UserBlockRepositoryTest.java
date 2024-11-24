package zip.ootd.ootdzip.userblock.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.userblock.domain.UserBlock;

@Transactional
class UserBlockRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserBlockRepository userBlockRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("차단 유무를 조회한다.")
    @Test
    void existUserBlock() {
        // given
        User blockUser = createdUserBy("차단한 유저", false);
        User blockedUser = createdUserBy("차단된 유저", false);

        UserBlock userBlock = blockUser(blockedUser, blockUser);

        // when
        Boolean result1 = userBlockRepository.existUserBlock(blockUser.getId(), blockedUser.getId());
        Boolean result2 = userBlockRepository.existUserBlock(blockedUser.getId(), blockUser.getId());

        //then
        assertThat(result1)
                .isTrue()
                .isEqualTo(result2);
    }

    @DisplayName("차단하지 않은 사용자끼리 차단 유무를 조회하면 false를 반환한다.")
    @Test
    void noExistUserBlock() {
        // given
        User blockUser = createdUserBy("차단한 유저", false);
        User blockedUser = createdUserBy("차단된 유저", false);

        // when
        Boolean result1 = userBlockRepository.existUserBlock(blockUser.getId(), blockedUser.getId());
        Boolean result2 = userBlockRepository.existUserBlock(blockedUser.getId(), blockUser.getId());

        //then
        assertThat(result1)
                .isFalse()
                .isEqualTo(result2);
    }

    @DisplayName("접근할 수 없는 userId를 조회하면 차단한 유저의 Id와 차단당한 유져의 Id를 조회한다.")
    @Test
    void getNonAccessibleUserIds() {
        // given
        User user = createdUserBy("유저", false);
        User blockedUser = createdUserBy("차단된 유저", false);
        User blockUser = createdUserBy("차단한 유저", false);

        blockUser(blockedUser, user);
        blockUser(user, blockUser);
        // when
        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(user.getId());

        //then
        assertThat(nonAccessibleUserIds).hasSize(2)
                .containsExactlyInAnyOrder(blockedUser.getId(), blockUser.getId());
    }

    @DisplayName("접근할 수 없는 userId를 조회할 때 서로 차단한 유저의 Id는 중복이 제거되어 조회된다.")
    @Test
    void getNonAccessibleUserIdsWithDuplicateUser() {
        // given
        User user = createdUserBy("유저", false);
        User blockedUser = createdUserBy("차단된 유저", false);

        blockUser(blockedUser, user);
        blockUser(user, blockedUser);
        // when
        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(user.getId());

        //then
        assertThat(nonAccessibleUserIds).hasSize(1)
                .containsExactlyInAnyOrder(blockedUser.getId());
    }

    private UserBlock blockUser(User blockedUser, User blockUser) {
        UserBlock userBlock = UserBlock.createBy(blockedUser, blockUser);
        return userBlockRepository.save(userBlock);
    }

    private User createdUserBy(String name, Boolean isDeleted) {
        User user = User.getDefault();
        user.setName(name);
        user.setIsDeleted(isDeleted);
        return userRepository.save(user);
    }
}
