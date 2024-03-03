package zip.ootd.ootdzip.user.domain;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.common.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserStyle extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private Style style;

    private static UserStyle createUserStyleBy(Style style, User user) {
        return UserStyle.builder()
                .style(style)
                .user(user)
                .build();
    }

    public static List<UserStyle> createUserStylesBy(List<Style> styles, User user) {
        return styles.stream()
                .map(style -> createUserStyleBy(style, user))
                .toList();
    }
}
