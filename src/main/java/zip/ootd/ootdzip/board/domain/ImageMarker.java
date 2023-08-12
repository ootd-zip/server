package zip.ootd.ootdzip.board.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.clothes.domain.Clothes;

@Entity
@Table(name = "image_markers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageMarker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "clothes_id", nullable = false)
    private Clothes clothes;
}
