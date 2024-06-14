package zip.ootd.ootdzip.user.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.request.CommonPageRequest;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.user.controller.response.ProfileRes;
import zip.ootd.ootdzip.user.controller.response.UserInfoForMyPageRes;
import zip.ootd.ootdzip.user.controller.response.UserSearchRes;
import zip.ootd.ootdzip.user.controller.response.UserStyleRes;
import zip.ootd.ootdzip.user.data.FollowReq;
import zip.ootd.ootdzip.user.data.UserSearchType;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;
import zip.ootd.ootdzip.user.domain.UserStyle;
import zip.ootd.ootdzip.user.repository.UserRepository;
import zip.ootd.ootdzip.user.repository.UserStyleRepository;
import zip.ootd.ootdzip.user.service.request.ProfileSvcReq;
import zip.ootd.ootdzip.user.service.request.UserInfoForMyPageSvcReq;
import zip.ootd.ootdzip.user.service.request.UserRegisterSvcReq;
import zip.ootd.ootdzip.user.service.request.UserSearchSvcReq;
import zip.ootd.ootdzip.user.service.request.UserStyleUpdateSvcReq;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private UserStyleRepository userStyleRepository;

    @Autowired
    private EntityManager em;

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
        User user1 = createUserBy("유저1");
        User loginUser = createUserBy("유저2");
        UserInfoForMyPageSvcReq request = UserInfoForMyPageSvcReq
                .builder()
                .userId(user1.getId())
                .build();

        userService.deleteUser(user1);
        em.flush();
        em.detach(user1);
        // when & then
        assertThatThrownBy(() -> userService.getUserInfoForMyPage(request, loginUser))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(403, "U003", "탈퇴된 사용자");
    }

    @DisplayName("탈퇴한 userId로 마이페이지 정보를 조회하면 에러가 발생한다.")
    @Test
    void getUserInfoForMyPageWithDeletedUserId() {
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

    @DisplayName("유저 프로필 정보를 조회한다.")
    @Test
    void getProfile() {
        // given
        User user = createUserBy("유저1");

        // when
        ProfileRes result = userService.getProfile(user);

        //then
        assertThat(result)
                .extracting("name",
                        "profileImage",
                        "description",
                        "height",
                        "weight",
                        "isBodyPrivate")
                .contains(user.getName(),
                        user.getProfileImage(),
                        user.getDescription(),
                        user.getHeight(),
                        user.getWeight(),
                        user.getIsBodyPrivate());
    }

    @DisplayName("유저 정보를 등록한다.")
    @Test
    void register() {
        // given
        User user = createDefaultUser();
        Style style1 = createStyleBy("스타일1");
        Style style2 = createStyleBy("스타일2");
        Style style3 = createStyleBy("스타일3");

        UserRegisterSvcReq request = UserRegisterSvcReq.builder()
                .name("유저1")
                .age(20)
                .gender(UserGender.MALE)
                .weight(70)
                .height(170)
                .isBodyPrivate(true)
                .styles(List.of(style1.getId(),
                        style2.getId(),
                        style3.getId()))
                .build();

        // when
        userService.register(request, user);

        //then
        User registeredUser = userRepository.findById(user.getId()).get();

        assertThat(registeredUser)
                .extracting("name",
                        "age",
                        "gender",
                        "weight",
                        "height",
                        "isBodyPrivate",
                        "isCompleted")
                .contains(user.getName(),
                        user.getAge(),
                        user.getGender(),
                        user.getWeight(),
                        user.getHeight(),
                        user.getIsBodyPrivate(),
                        user.getIsCompleted());

        assertThat(registeredUser.getUserStyles())
                .hasSize(3)
                .extracting("style.id", "style.name")
                .containsExactlyInAnyOrder(
                        tuple(style1.getId(), style1.getName()),
                        tuple(style2.getId(), style2.getName()),
                        tuple(style3.getId(), style3.getName()));

    }

    @DisplayName("이미 등록한 유저가 정보를 등록하면 에러가 발생한다.")
    @Test
    void registerWithCompletedUser() {
        // given
        User user = createDefaultUser();
        user.setIsCompleted(true);
        Style style1 = createStyleBy("스타일1");
        Style style2 = createStyleBy("스타일2");
        Style style3 = createStyleBy("스타일3");

        UserRegisterSvcReq request = UserRegisterSvcReq.builder()
                .name("유저1")
                .age(20)
                .gender(UserGender.MALE)
                .weight(70)
                .height(170)
                .isBodyPrivate(true)
                .styles(List.of(style1.getId(),
                        style2.getId(),
                        style3.getId()))
                .build();

        // when & then
        assertThatThrownBy(() -> userService.register(request, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(409, "U003", "회원가입이 완료된 유저입니다.");

    }

    @DisplayName("유효하지 않은 스타일 ID로 등록하면 에러가 발생한다.")
    @Test
    void registerWithInvalidStyle() {
        // given
        User user = createDefaultUser();
        Style style1 = createStyleBy("스타일1");
        Style style2 = createStyleBy("스타일2");
        Style style3 = createStyleBy("스타일3");

        UserRegisterSvcReq request = UserRegisterSvcReq.builder()
                .name("유저1")
                .age(20)
                .gender(UserGender.MALE)
                .weight(70)
                .height(170)
                .isBodyPrivate(true)
                .styles(List.of(style1.getId(),
                        style2.getId(),
                        style3.getId() + 100L))
                .build();

        // when & then
        assertThatThrownBy(() -> userService.register(request, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "S002", "유효하지 않은 사이즈 ID");

    }

    @DisplayName("유저 프로필 정보를 업데이트한다.")
    @Test
    void updateProfile() {
        // given
        User user = createDefaultUser();
        ProfileSvcReq request = ProfileSvcReq.builder()
                .name("유저1")
                .profileImage("image.jpg")
                .description("소개")
                .height(180)
                .weight(80)
                .isBodyPrivate(false)
                .build();

        // when
        userService.updateProfile(request, user);

        //then
        User updatedUser = userRepository.findById(user.getId()).get();

        assertThat(updatedUser)
                .extracting("name",
                        "profileImage",
                        "description",
                        "height",
                        "weight",
                        "isBodyPrivate")
                .contains("유저1",
                        "image.jpg",
                        "소개",
                        180,
                        80,
                        false);

    }

    @DisplayName("프로필 정보를 업데이트할 때 유효하지 않은 이미지로 저장하면 에러가 발생한다.")
    @Test
    void updateProfileWithInvalidImage() {
        // given
        User user = createDefaultUser();
        ProfileSvcReq request = ProfileSvcReq.builder()
                .name("유저1")
                .profileImage("image.fff")
                .description("소개")
                .height(180)
                .weight(80)
                .isBodyPrivate(false)
                .build();

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(request, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "I001", "이미지 URL이 유효하지 않습니다.");

    }

    @DisplayName("유저 선호 스타일을 조회한다.")
    @Test
    void getUserStyles() {
        // given
        User user = createDefaultUser();
        Style style1 = createStyleBy("스타일1");
        Style style2 = createStyleBy("스타일2");
        Style style3 = createStyleBy("스타일3");

        List<UserStyle> userStyles = UserStyle.createUserStylesBy(List.of(style1, style2, style3), user);

        userStyleRepository.saveAll(userStyles);

        // when
        List<UserStyleRes> result = userService.getUserStyle(user);

        //then
        assertThat(result).hasSize(3)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(style1.getId(), style1.getName()),
                        tuple(style2.getId(), style2.getName()),
                        tuple(style3.getId(), style3.getName()));
    }

    @DisplayName("유저 선호 스타일을 조회할 때 등록된 선호 스타일이 없을 때 빈 리스트를 반환한다.")
    @Test
    void getUserStylesWithNoRegisteredUserStyle() {
        // given
        User user = createDefaultUser();

        // when
        List<UserStyleRes> result = userService.getUserStyle(user);

        //then
        assertThat(result).hasSize(0)
                .isNotNull();
    }

    @DisplayName("유저 선호 스타일을 업데이트한다.")
    @Test
    void updateUserStyles() {
        // given
        User user = createDefaultUser();
        Style style1 = createStyleBy("스타일1");
        Style style2 = createStyleBy("스타일2");
        Style style3 = createStyleBy("스타일3");

        List<UserStyle> userStyles = UserStyle.createUserStylesBy(List.of(style1, style2, style3), user);
        userRepository.save(user);

        Style style4 = createStyleBy("스타일4");

        UserStyleUpdateSvcReq request = UserStyleUpdateSvcReq.builder()
                .styleIds(List.of(style1.getId(), style2.getId(), style4.getId()))
                .build();

        // when
        userService.updateUserStyles(request, user);
        //then
        List<UserStyle> result = userStyleRepository.findAllByUser(user);

        assertThat(result).hasSize(3)
                .extracting("style.id", "style.name")
                .containsExactlyInAnyOrder(
                        tuple(style1.getId(), style1.getName()),
                        tuple(style2.getId(), style2.getName()),
                        tuple(style4.getId(), style4.getName()));
    }

    @DisplayName("유저 선호 스타일이 등록되지 않은 유저의 선호 스타일을 업데이트한다.")
    @Test
    void updateUserStyleWithNoRegisteredUserStyle() {
        // given
        User user = createDefaultUser();
        userRepository.save(user);

        Style style1 = createStyleBy("스타일1");
        Style style2 = createStyleBy("스타일2");
        Style style3 = createStyleBy("스타일3");

        UserStyleUpdateSvcReq request = UserStyleUpdateSvcReq.builder()
                .styleIds(List.of(style1.getId(), style2.getId(), style3.getId()))
                .build();

        // when
        userService.updateUserStyles(request, user);
        //then
        List<UserStyle> result = userStyleRepository.findAllByUser(user);

        assertThat(result).hasSize(3)
                .extracting("style.id", "style.name")
                .containsExactlyInAnyOrder(
                        tuple(style1.getId(), style1.getName()),
                        tuple(style2.getId(), style2.getName()),
                        tuple(style3.getId(), style3.getName()));
    }

    @DisplayName("유효하지 않은 스타일로 유저 선호 스타일을 업데이트하면 에러가 발생한다.")
    @Test
    void updateUserStylesInvalidStyle() {
        // given
        User user = createDefaultUser();
        Style style1 = createStyleBy("스타일1");
        Style style2 = createStyleBy("스타일2");
        Style style3 = createStyleBy("스타일3");

        List<UserStyle> userStyles = UserStyle.createUserStylesBy(List.of(style1, style2, style3), user);
        userRepository.save(user);

        Style style4 = createStyleBy("스타일4");

        UserStyleUpdateSvcReq request = UserStyleUpdateSvcReq.builder()
                .styleIds(List.of(style1.getId(), style2.getId(), style4.getId() + 1))
                .build();

        // when
        assertThatThrownBy(() -> userService.updateUserStyles(request, user))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "S002", "유효하지 않은 사이즈 ID");

    }

    @DisplayName("나를 팔로워한 사람을 언팔한다.")
    @Test
    void unfollower() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        User user2 = createUserBy("유저2");
        userService.follow(user.getId(), user1.getId()); // 유저1 이 유저를 팔로우
        userService.follow(user.getId(), user2.getId()); // 유저2 이 유저를 팔로우

        FollowReq request = new FollowReq();
        request.setUserId(user1.getId());

        // when
        userService.removeFollower(user, request); // 유저가 유저1을 언팔로우

        // then
        User result = userRepository.findById(user.getId()).orElseThrow();
        assertThat(result.getFollowers())
                .hasSize(1)
                .extracting("id")
                .containsExactlyInAnyOrder(user2.getId());

        User result1 = userRepository.findById(user1.getId()).orElseThrow();
        assertThat(result1.getFollowings()).hasSize(0);
    }

    @DisplayName("나를 팔로우한 사람이 아닌 사람을 언팔로워할시 실패한다.")
    @Test
    void unfollowerNotFollower() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        User user2 = createUserBy("유저2");
        userService.follow(user.getId(), user2.getId()); // 유저2 이 유저를 팔로우

        FollowReq request = new FollowReq();
        request.setUserId(user1.getId());

        // when & then
        User result = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userService.removeFollower(user, request)).isEqualTo(false);
    }

    @DisplayName("유저의 팔로워를 기본 조회 한다.")
    @Test
    void searchFollowerByDefault() {
        // given
        User user = createUserBy("감자");
        User user1 = createUserBy("자감");
        User user2 = createUserBy("자감자");
        User user3 = createUserBy("자가비");

        userService.follow(user.getId(), user1.getId()); // user1 이 user 를 팔로우
        userService.follow(user.getId(), user2.getId()); // user2 이 user 를 팔로우
        CommonPageRequest pageRequest = new CommonPageRequest();

        UserSearchSvcReq userSearchSvcReq = UserSearchSvcReq.builder()
                .userSearchType(UserSearchType.FOLLOWER)
                .userId(user.getId())
                .name("")
                .pageable(pageRequest.toPageable())
                .build();

        // when
        CommonPageResponse<UserSearchRes> results = userService.searchUser(userSearchSvcReq, user3);

        // then
        assertThat(results.getContent()).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(tuple(user1.getId(), user1.getName()),
                        tuple(user2.getId(), user2.getName()));
        assertThat(results.getTotal()).isEqualTo(2);
    }

    @DisplayName("유저의 팔로잉을 기본 조회 한다.")
    @Test
    void searchFollowingByDefault() {
        // given
        User user = createUserBy("감자");
        User user1 = createUserBy("자감");
        User user2 = createUserBy("자감자");
        User user3 = createUserBy("자가비");
        User user4 = createUserBy("긴감자");

        userService.follow(user1.getId(), user.getId()); // user 이 user1 를 팔로우
        userService.follow(user2.getId(), user.getId()); // user 이 user2 를 팔로우
        userService.follow(user3.getId(), user.getId()); // user 이 user3 를 팔로우
        CommonPageRequest pageRequest = new CommonPageRequest();

        UserSearchSvcReq userSearchSvcReq = UserSearchSvcReq.builder()
                .userSearchType(UserSearchType.FOLLOWING)
                .userId(user.getId())
                .name("")
                .pageable(pageRequest.toPageable())
                .build();

        // when
        CommonPageResponse<UserSearchRes> results = userService.searchUser(userSearchSvcReq, user3);

        // then
        assertThat(results.getContent()).hasSize(3)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(tuple(user1.getId(), user1.getName()),
                        tuple(user2.getId(), user2.getName()),
                        tuple(user3.getId(), user3.getName()));
        assertThat(results.getTotal()).isEqualTo(3);
    }

    @DisplayName("계정을 삭제한다.")
    @Test
    void deleteUser() {
        // given
        User user1 = createUserBy("유저1");

        // when
        userService.deleteUser(user1);

        //then
        Optional<User> disjoinedUser = userRepository.findById(user1.getId());
        assertThat(disjoinedUser.get().getIsDeleted()).isTrue();
    }

    private User createDefaultUser() {
        return userRepository.save(User.getDefault());
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        user.setIsCompleted(true);
        return userRepository.save(user);
    }

    private Style createStyleBy(String styleName) {
        Style style = Style.builder()
                .name(styleName)
                .build();
        return styleRepository.save(style);
    }
}
