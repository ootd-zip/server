package zip.ootd.ootdzip.oauth.data;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GoogleOauthToken {

    private String accessToken;

    private int expiresIn;

    private String scope;

    private String tokenType;

    private String idToken;
}

