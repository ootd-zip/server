package zip.ootd.ootdzip.ootdbookmark.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OotdBookmark extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ootd_id")
    private Ootd ootd;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static OotdBookmark createOotdBookmarkBy(User user) {

        return OotdBookmark.builder()
                .user(user)
                .build();
    }
}
