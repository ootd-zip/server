package zip.ootd.ootdzip.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenInfo {

    private String tokenType;
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
    private Long refreshTokenExpiresIn;

}
