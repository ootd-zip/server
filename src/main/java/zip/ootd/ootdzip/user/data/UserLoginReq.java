package zip.ootd.ootdzip.user.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserLoginReq {

    @JsonProperty(required = true)
    private String redirectUri;

    @JsonProperty(required = true)
    private String oauthProvider;

    @JsonProperty(required = true)
    private String authorizationCode;
}
