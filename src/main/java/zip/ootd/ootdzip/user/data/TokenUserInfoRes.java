package zip.ootd.ootdzip.user.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import zip.ootd.ootdzip.user.domain.User;

@Data
@AllArgsConstructor
@Builder
public class TokenUserInfoRes {

    private Long id;

    public static TokenUserInfoRes of(User user) {
        return TokenUserInfoRes.builder()
                .id(user.getId())
                .build();
    }
}
