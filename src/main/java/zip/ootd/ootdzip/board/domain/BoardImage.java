package zip.ootd.ootdzip.board.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.BaseEntity;
import zip.ootd.ootdzip.board.domain.Board;

@Entity
@Table(name = "board_images")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardImage extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(length = 2048)
    private String imageUrl;
}
