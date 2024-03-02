package zip.ootd.ootdzip.user.service.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProfileSvcReq {

    private String name;
    private String profileImage;
    private String description;
    private Integer height;
    private Integer weight;
    private Boolean isBodyPrivate;
}
