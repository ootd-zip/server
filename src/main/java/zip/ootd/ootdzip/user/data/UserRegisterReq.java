package zip.ootd.ootdzip.user.data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import zip.ootd.ootdzip.user.domain.UserGender;

@Data
public class UserRegisterReq {

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private UserGender gender;

    @JsonProperty(required = true)
    private LocalDate birthdate;

    private Integer height;

    private Boolean showHeight;

    private Integer weight;

    private Boolean showWeight;
}
