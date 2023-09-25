package zip.ootd.ootdzip.boarduser.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardUser extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    private boolean isLike = false;

    public static BoardUser createBoardUserBy(User user) {

        return BoardUser.builder()
                .user(user)
                .build();
    }

    public boolean changeLike() {
        return isLike = !isLike;
    }
}
