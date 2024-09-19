package zip.ootd.ootdzip.oauth.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class LoginSvcReq {
    private String loginId;
    private String password;
}
