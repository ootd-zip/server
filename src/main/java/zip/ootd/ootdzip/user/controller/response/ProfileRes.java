package zip.ootd.ootdzip.user.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.user.domain.User;

@Getter
@NoArgsConstructor
public class ProfileRes {

    private String name;

    private String profileImage;

    private String description;

    private Integer height;

    private Integer weight;

    private Boolean isBodyPrivate;

    @Builder
    private ProfileRes(String name, String profileImage, String description, Integer height, Integer weight,
            Boolean isBodyPrivate) {
        this.name = name;
        this.profileImage = profileImage;
        this.description = description;
        this.height = height;
        this.weight = weight;
        this.isBodyPrivate = isBodyPrivate;
    }

    public static ProfileRes of(User loginUser) {
        return ProfileRes.builder()
                .name(loginUser.getName())
                .profileImage(loginUser.getProfileImage().getImageUrl173x173())
                .description(loginUser.getDescription())
                .height(loginUser.getProfileHeight(loginUser))
                .weight(loginUser.getProfileWeight(loginUser))
                .isBodyPrivate(loginUser.getIsBodyPrivate())
                .build();
    }

}
