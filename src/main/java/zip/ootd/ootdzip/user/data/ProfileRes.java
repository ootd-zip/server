package zip.ootd.ootdzip.user.data;

import lombok.Data;
import zip.ootd.ootdzip.user.domain.User;

@Data
public class ProfileRes {

    private String name;

    private String profileImage;

    private Integer followingCount;

    private Integer followerCount;

    private Integer clothesCount;

    private ProfileRes() {

    }

    public ProfileRes(User user) {

        name = user.getName();
        profileImage = user.getProfileImage();
        followingCount = user.getFollowings().size();
        followerCount = user.getFollowers().size();
        clothesCount = user.getClothesList().size();
    }
}
