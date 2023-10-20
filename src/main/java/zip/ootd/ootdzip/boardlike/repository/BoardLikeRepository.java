package zip.ootd.ootdzip.boardlike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.boardlike.domain.BoardLike;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    @Query("SELECT bl from BoardLike bl "
            + "where bl.board.id = :boardId "
            + "and bl.user.id = :userId ")
    BoardLike findByBoardIdAndUserId(@Param("boardId") String boardId, @Param("userId") String userId);
}
