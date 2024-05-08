package zip.ootd.ootdzip.userblock.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.userblock.domain.UserBlock;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;
import zip.ootd.ootdzip.userblock.service.request.BlockUserSvcReq;
import zip.ootd.ootdzip.userblock.service.request.UnBlockUserSvcReq;

class UserBlockServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserBlockService userBlockService;

    @Autowired
    private UserBlockRepository userBlockRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("사용자를 차단한다.")
    @Test
    void blockUser() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);
        User blockedUser2 = createdUserBy("차단된 유저2", false);
        User blockedUser3 = createdUserBy("차단된 유저3", false);

        BlockUserSvcReq request1 = BlockUserSvcReq.createBy(blockedUser1.getId());
        BlockUserSvcReq request2 = BlockUserSvcReq.createBy(blockedUser2.getId());
        BlockUserSvcReq request3 = BlockUserSvcReq.createBy(blockedUser3.getId());

        // when
        userBlockService.blockUser(request1, blockUser);
        userBlockService.blockUser(request2, blockUser);
        userBlockService.blockUser(request3, blockUser);
        //then
        Pageable pageable = PageRequest.of(0, 5);
        List<UserBlock> result = userBlockRepository.findAllByBlockUser(blockUser, pageable);

        assertThat(result).hasSize(3)
                .extracting("blockUser.id", "blockedUser.id")
                .containsExactlyInAnyOrder(
                        tuple(blockUser.getId(), blockedUser1.getId()),
                        tuple(blockUser.getId(), blockedUser2.getId()),
                        tuple(blockUser.getId(), blockedUser3.getId())
                );
    }

    @DisplayName("유효하지 않은 id의 사용자를 차단하면 에러가 발생한다.")
    @Test
    void blockUserWithInvalidUserId() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);

        BlockUserSvcReq request1 = BlockUserSvcReq.createBy(blockedUser1.getId() + 100L);

        // when & then
        assertThatThrownBy(() -> userBlockService.blockUser(request1, blockUser))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "U002", "유효하지 않은 유저 ID");
    }

    @DisplayName("탈퇴한 사용자를 차단하면 에러가 발생한다.")
    @Test
    void blockUserWithDeletedUser() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", true);

        BlockUserSvcReq request1 = BlockUserSvcReq.createBy(blockedUser1.getId());

        // when & then
        assertThatThrownBy(() -> userBlockService.blockUser(request1, blockUser))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(403, "U003", "탈퇴된 사용자");
    }

    @DisplayName("차단을 해제한다")
    @Test
    void unBlockUser() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);

        UserBlock userBlock = UserBlock.createBy(blockedUser1, blockUser);
        UserBlock savedUserBlock = userBlockRepository.save(userBlock);

        UnBlockUserSvcReq request = UnBlockUserSvcReq.builder()
                .id(savedUserBlock.getId())
                .build();
        // when
        userBlockService.unBlockUser(request, blockUser);

        //then
        List<UserBlock> allByBlockUser = userBlockRepository.findAllByBlockUser(blockUser, PageRequest.of(0, 10));

        assertThat(allByBlockUser).isEmpty();
    }

    @DisplayName("유효하지 않은 사용자 차단 ID로 차단 해제하면 에러가 발생한다.")
    @Test
    void unBlockUserWithInvalidUserBlockId() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);

        UserBlock userBlock = UserBlock.createBy(blockedUser1, blockUser);
        UserBlock savedUserBlock = userBlockRepository.save(userBlock);

        UnBlockUserSvcReq request = UnBlockUserSvcReq.builder()
                .id(savedUserBlock.getId() + 1)
                .build();

        // when & then
        assertThatThrownBy(() -> userBlockService.unBlockUser(request, blockUser))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "UB001", "유효하지 않은 사용자 차단 ID");
    }

    @DisplayName("유효하지 않은 사용자 차단 ID로 차단 해제하면 에러가 발생한다.")
    @Test
    void unBlockUserWithDifferentUser() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User diffUser = createdUserBy("유저2", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);

        UserBlock userBlock = UserBlock.createBy(blockedUser1, blockUser);
        UserBlock savedUserBlock = userBlockRepository.save(userBlock);

        UnBlockUserSvcReq request = UnBlockUserSvcReq.builder()
                .id(savedUserBlock.getId())
                .build();

        // when & then
        assertThatThrownBy(() -> userBlockService.unBlockUser(request, diffUser))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(403, "UB002", "본인만 차단을 해제할 수 았습니다.");
    }

    private User createdUserBy(String name, Boolean isDeleted) {
        User user = User.getDefault();
        user.setName(name);
        user.setIsDeleted(isDeleted);
        return userRepository.save(user);
    }
}
