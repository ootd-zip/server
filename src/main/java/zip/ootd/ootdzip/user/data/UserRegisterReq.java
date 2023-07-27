package zip.ootd.ootdzip.user.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import zip.ootd.ootdzip.user.domain.UserGender;

import java.time.LocalDate;

@Data
public class UserRegisterReq {

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private UserGender gender;

    @JsonProperty(required = true)
    private LocalDate birthdate;

    private Long height;

    private Boolean showHeight;

    private Long weight;

    private Boolean showWeight;

    @JsonProperty(required = true)
    private String oauthProvider;

    @JsonProperty(required = true)
    private String authorizationCode;
}
