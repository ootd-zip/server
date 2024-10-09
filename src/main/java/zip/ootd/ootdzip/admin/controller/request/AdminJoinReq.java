package zip.ootd.ootdzip.admin.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.admin.service.request.AdminJoinSvcReq;

@Getter
@NoArgsConstructor
public class AdminJoinReq {

    private String loginId;
    private String password;

    public AdminJoinSvcReq toServiceRequest() {
        return AdminJoinSvcReq.builder()
                .loginId(loginId)
                .password(password)
                .build();
    }
}
