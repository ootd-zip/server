package zip.ootd.ootdzip.user.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.user.domain.User;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserInfoForMyPageRes {

    private Long userId;
    private String userName;
    private String profileImage;
    private Long followerCount;
    private Long followingCount;
    private Integer height;
    private Integer weight;
    private String description;
    private Boolean isMyProfile;
    private Boolean isFollow;
    private Long ootdCount;
    private Long clothesCount;

    public static UserInfoForMyPageRes of(User user, User loginUser) {
        return UserInfoForMyPageRes.builder()
                .userId(user.getId())
                .userName(user.getName())
                .profileImage(user.getProfileImage())
                .followerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .height(user.getProfileHeight(loginUser))
                .weight(user.getProfileWeight(loginUser))
                .description(user.getDescription())
                .isMyProfile(user.equals(loginUser))
                .isFollow(loginUser.isFollowing(user))
                .ootdCount(user.getOotdsCount(loginUser))
                .clothesCount(user.getClothesCount(loginUser))
                .build();
    }
}
