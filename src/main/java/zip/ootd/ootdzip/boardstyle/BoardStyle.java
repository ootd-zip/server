package zip.ootd.ootdzip.boardstyle;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.common.entity.BaseEntity;

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
