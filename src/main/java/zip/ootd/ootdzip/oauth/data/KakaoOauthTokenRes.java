package zip.ootd.ootdzip.oauth.data;

import lombok.Data;

@Data
public class KakaoOauthTokenRes {

    @SuppressWarnings("checkstyle:MemberName")
    private String token_type;

    @SuppressWarnings("checkstyle:MemberName")
    private String access_token;

    @SuppressWarnings("checkstyle:MemberName")
    private Integer expires_in;

    @SuppressWarnings("checkstyle:MemberName")
    private String refresh_token;

    @SuppressWarnings("checkstyle:MemberName")
    private Integer refresh_token_expires_in;
}
