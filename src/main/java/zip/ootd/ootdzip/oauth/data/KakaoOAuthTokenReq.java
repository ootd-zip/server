package zip.ootd.ootdzip.oauth.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoOAuthTokenReq {

    private String grantType;

    private String clientId;

    private String redirectUri;

    private String code;
}
