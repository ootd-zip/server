package zip.ootd.ootdzip.oauth.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleAccessTokenInfoRes {

    public String id;

    public String email;

    public Boolean verifiedEmail;

    public String name;

    public String givenName;

    public String familyName;

    public String picture;

    public String locale;
}
