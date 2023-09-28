package zip.ootd.ootdzip.boardclothe.domain;

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
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.common.entity.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardClothes extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "clothe_id")
    private Clothes clothes;

    public static BoardClothes createBoardClothesBy(Clothes clothes) {

        return BoardClothes.builder()
                .clothes(clothes)
                .build();
    }

    public static List<BoardClothes> createBoardClothesListBy(List<Clothes> clothesList) {

        return clothesList.stream()
                .map(BoardClothes::createBoardClothesBy)
                .collect(Collectors.toList());
    }
}
