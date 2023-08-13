package zip.ootd.ootdzip.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.entity.BaseEntity;

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
