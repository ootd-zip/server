package zip.ootd.ootdzip.boardbookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.boardbookmark.domain.BoardBookmark;

public interface BoardBookmarkRepository extends JpaRepository<BoardBookmark, Long> {

    @Query("SELECT bl from BoardLike bl "
            + "where bl.board.id = :boardId "
            + "and bl.user.id = :userId ")
    BoardBookmark findByBoardIdAndUserId(@Param("boardId") Long boardId, @Param("userId") Long userId);
}
