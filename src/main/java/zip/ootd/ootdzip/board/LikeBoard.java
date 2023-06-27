package zip.ootd.ootdzip.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.BaseEntity;
import zip.ootd.ootdzip.user.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "like_boards")
public class LikeBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}
