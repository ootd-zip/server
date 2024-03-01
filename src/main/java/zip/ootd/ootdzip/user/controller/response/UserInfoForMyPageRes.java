package zip.ootd.ootdzip.user.controller.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.user.domain.User;

@Data
@NoArgsConstructor
public class UserInfoForMyPageRes {

    private Long userId;
    private String userName;
    private String profileImage;
    private Long followerCount;
    private Long followingCount;
    private String height;
    private String weight;
    private String description;
    private Boolean isMyProfile;
    private Boolean isFollow;
    private Long ootdCount;
    private Long clothesCount;

    @Builder
    private UserInfoForMyPageRes(Long userId, String userName, Long followerCount, Long followingCount, String height,
            String weight, String description, Boolean isMyProfile, Boolean isFollow, Long ootdCount,
            Long clothesCount) {
        this.userId = userId;
        this.userName = userName;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.height = height;
        this.weight = weight;
        this.description = description;
        this.isMyProfile = isMyProfile;
        this.isFollow = isFollow;
        this.ootdCount = ootdCount;
        this.clothesCount = clothesCount;
    }

    public static UserInfoForMyPageRes of(User user, User loginUser) {
        return UserInfoForMyPageRes.builder()
                .userId(user.getId())
                .userName(user.getName())
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
