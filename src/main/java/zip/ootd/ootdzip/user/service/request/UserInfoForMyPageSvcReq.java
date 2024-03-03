package zip.ootd.ootdzip.user.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoForMyPageSvcReq {

    private Long userId;

    @Builder
    private UserInfoForMyPageSvcReq(Long userId) {
        this.userId = userId;
    }

    public static UserInfoForMyPageSvcReq createBy(Long userId) {
        return UserInfoForMyPageSvcReq.builder()
                .userId(userId)
                .build();
    }
}
