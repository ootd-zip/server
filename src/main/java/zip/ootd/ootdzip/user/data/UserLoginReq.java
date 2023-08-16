package zip.ootd.ootdzip.user.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import zip.ootd.ootdzip.oauth.domain.OauthProvider;

@Data
public class UserLoginReq {

    @JsonProperty(required = true)
    private String redirectUri;

    @JsonProperty(required = true)
    private OauthProvider oauthProvider;

    @JsonProperty(required = true)
    private String authorizationCode;
}
