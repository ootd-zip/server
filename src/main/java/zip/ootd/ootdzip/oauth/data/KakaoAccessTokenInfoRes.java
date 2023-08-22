package zip.ootd.ootdzip.oauth.data;

import lombok.Data;

@Data
public class KakaoAccessTokenInfoRes {

    private Long id;

    private Integer expiresIn;

    private Integer appId;
}
