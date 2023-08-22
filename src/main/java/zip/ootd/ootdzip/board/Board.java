package zip.ootd.ootdzip.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

import java.util.List;

@Entity
@Table(name = "boards")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;
    private String contents;
    @Column(nullable = false)
    private Integer viewCount = 0;
    @Column(nullable = false)
    private Boolean isDeleted = false;
    @Column(nullable = false)
    private Boolean isBlocked = false;
    @Column(nullable = false)
    private Integer reportCount = 0;
    @Column(nullable = false)
    private Integer likeCount = 0;
    @OneToMany
    @JoinColumn(name = "board_id")
    private List<BoardImage> boardImages;

    public void writeBoard(User user, String contents, List<BoardImage> boardImages){
        this.writer = user;
        this.contents = contents;
        this.boardImages = boardImages;
    }
}
