package zip.ootd.ootdzip.user.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.user.domain.User;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class UserSearchRes {
    private Long id;
    private String profileImage;
    private String name;
    private Boolean isFollow;

    public static UserSearchRes of(User user, User loginUser) {
        return UserSearchRes.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .isFollow(loginUser.isFollowing(user))
                .build();
    }
}
