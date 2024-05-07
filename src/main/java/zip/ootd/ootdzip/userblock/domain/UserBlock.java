package zip.ootd.ootdzip.userblock.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
public class UserBlock extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User blockedUser;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User blockUser;
}
