package zip.ootd.ootdzip.admin.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.oauth.service.request.LoginSvcReq;

@Getter
@NoArgsConstructor
public class AdminLoginReq {
    private String loginId;
    private String password;

    public LoginSvcReq toServiceRequest() {
        return LoginSvcReq.builder()
                .loginId(loginId)
                .password(password)
                .build();
    }
}
