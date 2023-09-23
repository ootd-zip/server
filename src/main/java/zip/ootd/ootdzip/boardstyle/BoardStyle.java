package zip.ootd.ootdzip.boardstyle;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.common.entity.BaseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardStyle extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private Style style;

    public static BoardStyle createBoardStyleBy(Style style) {

        return BoardStyle.builder()
                .style(style)
                .build();
    }

    public static List<BoardStyle> createBoardStylesBy(List<Style> styles) {

        return styles.stream()
                .map(BoardStyle::createBoardStyleBy)
                .collect(Collectors.toList());
    }
}
