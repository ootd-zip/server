package zip.ootd.ootdzip.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.common.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
public class UserStyle extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private Style style;

    @Builder
    private UserStyle(User user, Style style) {
        this.user = user;
        this.style = style;
    }
}
