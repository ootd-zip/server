package zip.ootd.ootdzip.user.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.user.controller.response.UserInfoForMyPageRes;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.user.service.request.UserInfoForMyPageSvcReq;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @DisplayName("userId로 마이페이지에서 사용하는 유저 정보를 조회한다.")
    @Test
    void getUserInfoForMyPage() {
        // given
        User user1 = createUserBy("유저1");
        User loginUser = createUserBy("유저2");
        UserInfoForMyPageSvcReq request = UserInfoForMyPageSvcReq
                .builder()
                .userId(user1.getId())
                .build();

        // when
        UserInfoForMyPageRes result = userService.getUserInfoForMyPage(request, loginUser);

        //then
        assertThat(result)
                .extracting("userId",
                        "userName",
                        "profileImage",
                        "followerCount",
                        "followingCount",
                        "height",
                        "weight",
                        "description",
                        "isMyProfile",
                        "isFollow",
                        "ootdCount",
                        "clothesCount")
                .contains(user1.getId(),
                        user1.getName(),
                        user1.getProfileImage(),
                        user1.getFollowerCount(),
                        user1.getFollowingCount(),
                        user1.getProfileHeight(loginUser),
                        user1.getProfileWeight(loginUser),
                        user1.getDescription(),
                        false,
                        false,
                        user1.getOotdsCount(loginUser),
                        user1.getClothesCount(loginUser));
    }

    @DisplayName("본인의 userId로 마이페이이지 정보를 조회하면 isMyProfile이 true이다.")
    @Test
    void getUserInfoForMyPageWithMyUserId() {
        // given
        User loginUser = createUserBy("유저1");
        UserInfoForMyPageSvcReq request = UserInfoForMyPageSvcReq
                .builder()
                .userId(loginUser.getId())
                .build();

        // when
        UserInfoForMyPageRes result = userService.getUserInfoForMyPage(request, loginUser);

        //then
        assertThat(result)
                .extracting("userId",
                        "userName",
                        "profileImage",
                        "followerCount",
                        "followingCount",
                        "height",
                        "weight",
                        "description",
                        "isMyProfile",
                        "isFollow",
                        "ootdCount",
                        "clothesCount")
                .contains(loginUser.getId(),
                        loginUser.getName(),
                        loginUser.getProfileImage(),
                        loginUser.getFollowerCount(),
                        loginUser.getFollowingCount(),
                        loginUser.getProfileHeight(loginUser),
                        loginUser.getProfileWeight(loginUser),
                        loginUser.getDescription(),
                        true,
                        false,
                        loginUser.getOotdsCount(loginUser),
                        loginUser.getClothesCount(loginUser));
    }

    @DisplayName("팔로우한 userId로 마이페이지 정보를 조회하면 isFollow가 true이다.")
    @Test
    void getUserInfoForMyPageWithFollwUser() {
        // given
        User user1 = createUserBy("유저1");
        User loginUser = createUserBy("유저2");
        UserInfoForMyPageSvcReq request = UserInfoForMyPageSvcReq
                .builder()
                .userId(user1.getId())
                .build();

        userService.follow(user1.getId(), loginUser.getId());
        // when
        UserInfoForMyPageRes result = userService.getUserInfoForMyPage(request, loginUser);

        //then
        assertThat(result)
                .extracting("userId",
                        "userName",
                        "profileImage",
                        "followerCount",
                        "followingCount",
                        "height",
                        "weight",
                        "description",
                        "isMyProfile",
                        "isFollow",
                        "ootdCount",
                        "clothesCount")
                .contains(user1.getId(),
                        user1.getName(),
                        user1.getProfileImage(),
                        user1.getFollowerCount(),
                        user1.getFollowingCount(),
                        user1.getProfileHeight(loginUser),
                        user1.getProfileWeight(loginUser),
                        user1.getDescription(),
                        false,
                        true,
                        user1.getOotdsCount(loginUser),
                        user1.getClothesCount(loginUser));
    }

    @DisplayName("유효하지 않은 userId로 마이페이지 정보를 조회하면 에러가 발생한다.")
    @Test
    void getUserInfoForMyPageWithInvalidUserId() {
        // given
        User loginUser = createUserBy("유저1");
        UserInfoForMyPageSvcReq request = UserInfoForMyPageSvcReq
                .builder()
                .userId(Long.MAX_VALUE)
                .build();

        // when & then
        assertThatThrownBy(() -> userService.getUserInfoForMyPage(request, loginUser))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "U002", "유효하지 않은 유저 ID");
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }

}
