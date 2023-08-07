package zip.ootd.ootdzip.oauth.data;

import lombok.Data;

@Data
public class KakaoOAuthTokenRes {

    private String token_type;

    private String access_token;

    private Integer expires_in;

    private String refresh_token;

    private Integer refresh_token_expires_in;
}
