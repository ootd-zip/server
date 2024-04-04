package zip.ootd.ootdzip.user.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.common.request.CommonPageRequest;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

public class UserRepositoryImplTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @DisplayName("유저를 검색한다.")
    @Test
    void searchUser() {
        // given
        User user = createUserBy("감자");
        User user1 = createUserBy("자감");
        User user2 = createUserBy("자감자");

        CommonPageRequest pageRequest = new CommonPageRequest();

        // when
        Slice<User> results = userRepository.searchUsers("감자", pageRequest.toPageable());

        // then
        assertThat(results).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(tuple(user.getId(), user.getName()),
                        tuple(user2.getId(), user2.getName()));
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

        // when
        Slice<User> results = userRepository.searchFollowers("", user, pageRequest.toPageable());

        // then
        assertThat(results).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(tuple(user1.getId(), user1.getName()),
                        tuple(user2.getId(), user2.getName()));
    }

    @DisplayName("유저의 팔로워를 검색 한다.")
    @Test
    void searchFollower() {
        // given
        User user = createUserBy("감자");
        User user1 = createUserBy("자감");
        User user2 = createUserBy("자감자");
        User user3 = createUserBy("고가비");
        User user4 = createUserBy("기감");

        userService.follow(user.getId(), user1.getId()); // user1 이 user 를 팔로우
        userService.follow(user.getId(), user2.getId()); // user2 이 user 를 팔로우
        userService.follow(user.getId(), user3.getId()); // user3 이 user 를 팔로우
        CommonPageRequest pageRequest = new CommonPageRequest();

        // when
        Slice<User> results = userRepository.searchFollowers("감", user, pageRequest.toPageable());

        // then
        assertThat(results).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(tuple(user1.getId(), user1.getName()),
                        tuple(user2.getId(), user2.getName()));
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

        // when
        Slice<User> results = userRepository.searchFollowings("", user, pageRequest.toPageable());

        // then
        assertThat(results).hasSize(3)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(tuple(user1.getId(), user1.getName()),
                        tuple(user2.getId(), user2.getName()),
                        tuple(user3.getId(), user3.getName()));
    }

    @DisplayName("유저의 팔로잉을 검색 한다.")
    @Test
    void searchFollowing() {
        // given
        User user = createUserBy("감자");
        User user1 = createUserBy("자감");
        User user2 = createUserBy("자감자");
        User user3 = createUserBy("자가비");

        userService.follow(user1.getId(), user.getId()); // user 이 user1 를 팔로우
        userService.follow(user2.getId(), user.getId()); // user 이 user2 를 팔로우
        CommonPageRequest pageRequest = new CommonPageRequest();

        // when
        Slice<User> results = userRepository.searchFollowings("감", user, pageRequest.toPageable());

        // then
        assertThat(results).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(tuple(user1.getId(), user1.getName()),
                        tuple(user2.getId(), user2.getName()));
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
