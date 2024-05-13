package zip.ootd.ootdzip.userblock.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserBlock extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "blocked_user_id")
    private User blockedUser;

    @ManyToOne
    @JoinColumn(name = "block_user_id")
    private User blockUser;

    public static UserBlock createBy(User blockedUser, User blockUser) {
        return UserBlock.builder()
                .blockedUser(blockedUser)
                .blockUser(blockUser)
                .build();
    }

}
