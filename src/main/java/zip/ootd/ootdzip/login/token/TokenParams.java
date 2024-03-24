package zip.ootd.ootdzip.login.token;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenParams {

    private TokenType tokenType;
    private String issuer;
    private String audience;
    private String userId;
    private Duration tokenTimeToLive;
}
