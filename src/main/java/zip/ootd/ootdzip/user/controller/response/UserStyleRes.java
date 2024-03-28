package zip.ootd.ootdzip.user.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.category.domain.Style;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class UserStyleRes {
    private Long id;
    private String name;

    public static UserStyleRes of(Style style) {
        return UserStyleRes.builder()
                .id(style.getId())
                .name(style.getName())
                .build();
    }
}
