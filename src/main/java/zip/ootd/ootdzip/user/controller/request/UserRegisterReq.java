package zip.ootd.ootdzip.user.controller.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.valid.EnumValid;
import zip.ootd.ootdzip.user.domain.UserGender;
import zip.ootd.ootdzip.user.service.request.UserRegisterSvcReq;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserRegisterReq {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String name;

    @EnumValid(enumClass = UserGender.class, message = "성별이 유효하지 않습니다.")
    private UserGender gender;

    @Positive(message = "나이는 양수여야 합니다.")
    private Integer age;

    @Positive(message = "키는 양수여야 합니다.")
    private Integer height;

    @Positive(message = "몸무게는 양수여야 합니다.")
    private Integer weight;

    @NotNull(message = "체형정보 공개여부는 필수입니다.")
    private Boolean isBodyPrivate;

    @Size(min = 3, message = "스타일은 3개 이상 입력해주세요.")
    private List<@Positive(message = "스타일 id는 양수여야 합니다.") Long> styles;

    public UserRegisterSvcReq toServiceRequest() {
        return UserRegisterSvcReq.builder()
                .name(name)
                .gender(gender)
                .age(age)
                .height(height)
                .weight(weight)
                .isBodyPrivate(isBodyPrivate)
                .styles(styles)
                .build();
    }
}
