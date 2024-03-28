package zip.ootd.ootdzip.user.controller.request;

import java.util.List;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.user.service.request.UserStyleUpdateSvcReq;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserStyleUpdateReq {

    @Size(min = 3, message = "스타일은 3개 이상 입력해주세요.")
    private List<@Positive(message = "스타일 ID는 양수여야 합니다.") Long> styleIds;

    public UserStyleUpdateSvcReq toServiceRequest() {
        return UserStyleUpdateSvcReq.builder()
                .styleIds(styleIds)
                .build();
    }

}
