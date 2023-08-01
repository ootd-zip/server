package zip.ootd.ootdzip.board.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.user.User;

@Entity
@Table(name = "report_boards")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportBoard {
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
