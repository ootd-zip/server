package zip.ootd.ootdzip.oauth.data;

import lombok.Data;

@Data
public class KakaoOAuthTokenRes {

    private String tokenType;

    private String accessToken;

    private Integer expiresIn;

    private String refreshToken;

    private Integer refreshTokenExpiresIn;
}
