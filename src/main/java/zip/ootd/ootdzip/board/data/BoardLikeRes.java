package zip.ootd.ootdzip.board.data;

import lombok.Data;

@Data
public class BoardLikeRes {

    /**
     * change 가 true : 현재 상태가 좋아요
     * change 가 fale : 현재 상태가 좋아요가 아님
     */
    private boolean change;

    public BoardLikeRes(boolean change) {
        this.change = change;
    }
}
