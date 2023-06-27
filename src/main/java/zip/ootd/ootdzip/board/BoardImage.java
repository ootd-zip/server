package zip.ootd.ootdzip.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board_images")
public class BoardImage extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
    @Column(length = 2048)
    private String imageUrl;
}
