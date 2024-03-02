package zip.ootd.ootdzip.user.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileSvcReq {
    private final String name;
    private final String profileImage;
    private final String description;
    private final Integer height;
    private final Integer weight;
    private final Boolean isBodyPrivate;

    @Builder
    private ProfileSvcReq(String name, String profileImage, String description, Integer height, Integer weight,
            Boolean isBodyPrivate) {
        this.name = name;
        this.profileImage = profileImage;
        this.description = description;
        this.height = height;
        this.weight = weight;
        this.isBodyPrivate = isBodyPrivate;
    }
}
