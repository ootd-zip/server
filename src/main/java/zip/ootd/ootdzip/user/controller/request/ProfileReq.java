package zip.ootd.ootdzip.user.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.user.service.request.ProfileSvcReq;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProfileReq {

    @NotBlank(message = "닉네임은 필수입니다.")
    private String name;

    private String profileImage;

    @Size(max = 2001, message = "소개는 최대 2000자 입니다.")
    private String description;

    @Positive(message = "키는 양수여야 합니다.")
    private Integer height;

    @Positive(message = "몸무게는 양수여야 합니다.")
    private Integer weight;

    @NotNull(message = "체형정보 공개여부는 필수입니다.")
    private Boolean isBodyPrivate;

    public ProfileSvcReq toServiceRequest() {
        return ProfileSvcReq.builder()
                .name(name)
                .profileImage(profileImage)
                .description(description)
                .height(height)
                .weight(weight)
                .isBodyPrivate(isBodyPrivate)
                .build();
    }
}
