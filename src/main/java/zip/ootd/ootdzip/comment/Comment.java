package zip.ootd.ootdzip.comment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.BaseEntity;
import zip.ootd.ootdzip.board.Board;
import zip.ootd.ootdzip.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Comments")
public class Comment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;
    private String contents;
    @Column(nullable = false)
    private Boolean isDeleted = false;
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    private Comment parent;
    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();
}
