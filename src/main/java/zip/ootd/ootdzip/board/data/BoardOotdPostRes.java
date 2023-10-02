package zip.ootd.ootdzip.board.data;

import lombok.Data;
import zip.ootd.ootdzip.board.domain.Board;

@Data
public class BoardOotdPostRes {

    private Long id;

    public BoardOotdPostRes(Board board) {
        this.id = board.getId();
    }
}
