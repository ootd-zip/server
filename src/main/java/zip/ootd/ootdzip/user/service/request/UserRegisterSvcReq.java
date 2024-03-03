package zip.ootd.ootdzip.user.service.request;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.user.domain.UserGender;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserRegisterSvcReq {

    private String name;
    private UserGender gender;
    private Integer age;
    private Integer height;
    private Integer weight;
    private Boolean isBodyPrivate;
    private List<Long> styles;

}
