package zip.ootd.ootdzip.userblock.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.request.CommonPageRequest;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.userblock.controller.response.UserBlockGetRes;
import zip.ootd.ootdzip.userblock.domain.UserBlock;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;
import zip.ootd.ootdzip.userblock.service.request.UserBlockGetSvcReq;
import zip.ootd.ootdzip.userblock.service.request.UserBlockSvcReq;
import zip.ootd.ootdzip.userblock.service.request.UserBlockUnBlockSvcReq;

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

        UserBlockSvcReq request1 = UserBlockSvcReq.createBy(blockedUser1.getId());
        UserBlockSvcReq request2 = UserBlockSvcReq.createBy(blockedUser2.getId());
        UserBlockSvcReq request3 = UserBlockSvcReq.createBy(blockedUser3.getId());

        // when
        userBlockService.blockUser(request1, blockUser);
        userBlockService.blockUser(request2, blockUser);
        userBlockService.blockUser(request3, blockUser);
        //then
        Pageable pageable = PageRequest.of(0, 5);
        Slice<UserBlock> result = userBlockRepository.findAllByBlockUser(blockUser, pageable);

        assertThat(result.getContent()).hasSize(3)
                .extracting("blockUser.id", "blockedUser.id")
                .containsExactlyInAnyOrder(tuple(blockUser.getId(), blockedUser1.getId()),
                        tuple(blockUser.getId(), blockedUser2.getId()), tuple(blockUser.getId(), blockedUser3.getId()));
    }

    @DisplayName("유효하지 않은 id의 사용자를 차단하면 에러가 발생한다.")
    @Test
    void blockUserWithInvalidUserId() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);

        UserBlockSvcReq request1 = UserBlockSvcReq.createBy(blockedUser1.getId() + 100L);

        // when & then
        assertThatThrownBy(() -> userBlockService.blockUser(request1, blockUser)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "U002", "유효하지 않은 유저 ID");
    }

    @DisplayName("탈퇴한 사용자를 차단하면 에러가 발생한다.")
    @Test
    void blockUserWithDeletedUser() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", true);

        UserBlockSvcReq request1 = UserBlockSvcReq.createBy(blockedUser1.getId());

        // when & then
        assertThatThrownBy(() -> userBlockService.blockUser(request1, blockUser)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(403, "U003", "탈퇴된 사용자");
    }

    @DisplayName("동일한 사용자를 차단하면 에러가 발생한다.")
    @Test
    void blockUserWithExistUserBlock() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);

        UserBlockSvcReq request1 = UserBlockSvcReq.createBy(blockedUser1.getId());
        userBlockService.blockUser(request1, blockUser);

        // when & then
        assertThatThrownBy(() -> userBlockService.blockUser(request1, blockUser)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(200, "UB003", "이미 차단한 유저입니다.");
    }

    @DisplayName("차단을 해제한다")
    @Test
    void unBlockUser() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);

        UserBlock savedUserBlock = blockUser(blockedUser1, blockUser);

        UserBlockUnBlockSvcReq request = UserBlockUnBlockSvcReq.builder().id(savedUserBlock.getId()).build();
        // when
        userBlockService.unBlockUser(request, blockUser);

        //then
        Slice<UserBlock> allByBlockUser = userBlockRepository.findAllByBlockUser(blockUser, PageRequest.of(0, 10));

        assertThat(allByBlockUser).isEmpty();
    }

    @DisplayName("유효하지 않은 사용자 차단 ID로 차단 해제하면 에러가 발생한다.")
    @Test
    void unBlockUserWithInvalidUserBlockId() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);

        UserBlock savedUserBlock = blockUser(blockedUser1, blockUser);

        UserBlockUnBlockSvcReq request = UserBlockUnBlockSvcReq.builder().id(savedUserBlock.getId() + 1).build();

        // when & then
        assertThatThrownBy(() -> userBlockService.unBlockUser(request, blockUser)).isInstanceOf(CustomException.class)
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

        UserBlock savedUserBlock = blockUser(blockedUser1, blockUser);

        UserBlockUnBlockSvcReq request = UserBlockUnBlockSvcReq.builder().id(savedUserBlock.getId()).build();

        // when & then
        assertThatThrownBy(() -> userBlockService.unBlockUser(request, diffUser)).isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(403, "UB002", "본인만 차단을 해제할 수 았습니다.");
    }

    @DisplayName("차단한 사용자를 조회한다.")
    @Test
    void getBlockedUser() {
        // given
        User blockUser = createdUserBy("유저1", false);
        User blockedUser1 = createdUserBy("차단된 유저1", false);
        User blockedUser2 = createdUserBy("차단된 유저2", false);
        User blockedUser3 = createdUserBy("차단된 유저3", false);

        UserBlock userBlock1 = blockUser(blockedUser1, blockUser);
        UserBlock userBlock2 = blockUser(blockedUser2, blockUser);
        UserBlock userBlock3 = blockUser(blockedUser3, blockUser);

        CommonPageRequest pageRequest = new CommonPageRequest(0, 2, "createdAt", Sort.Direction.DESC);

        UserBlockGetSvcReq request = UserBlockGetSvcReq.of(pageRequest);

        // when
        CommonSliceResponse<UserBlockGetRes> result = userBlockService.getUserBlocks(request, blockUser);

        //then
        assertThat(result.getContent()).hasSize(2)
                .extracting("userId", "userName", "profileImage")
                .containsExactlyInAnyOrder(
                        tuple(blockedUser2.getId(), blockedUser2.getName(),
                                blockedUser2.getProfileImage().getImageUrl70x70()),
                        tuple(blockedUser3.getId(), blockedUser3.getName(),
                                blockedUser3.getProfileImage().getImageUrl70x70()));

        assertThat(result.getIsLast()).isFalse();
    }

    @DisplayName("차단한 사용자를 조회할 때 차단한 사용자가 없으면 빈 리스트를 반환한다.")
    @Test
    void getEmptyBlockedUserWithNoBlockedUser() {
        // given
        User blockUser = createdUserBy("유저1", false);

        CommonPageRequest pageRequest = new CommonPageRequest(0, 2);

        UserBlockGetSvcReq request = UserBlockGetSvcReq.of(pageRequest);

        // when
        CommonSliceResponse<UserBlockGetRes> result = userBlockService.getUserBlocks(request, blockUser);

        //then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getIsLast()).isTrue();
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
