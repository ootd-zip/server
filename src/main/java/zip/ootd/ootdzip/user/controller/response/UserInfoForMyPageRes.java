package zip.ootd.ootdzip.user.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
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
    private Integer ootdCount;
    private Integer clothesCount;

    @Builder
    private UserInfoForMyPageRes(Long userId, String userName, Long followerCount, Long followingCount, String height,
            String weight, String description, Boolean isMyProfile, Boolean isFollow, Integer ootdCount,
            Integer clothesCount) {
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
}
