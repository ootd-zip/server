package zip.ootd.ootdzip.comment;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "Comments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
