package zip.ootd.ootdzip.user.service.request;

import lombok.Builder;

public class UserInfoForMyPageSvcReq {

    private final Long userId;

    @Builder
    private UserInfoForMyPageSvcReq(Long userId) {
        this.userId = userId;
    }
}
