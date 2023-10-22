package zip.ootd.ootdzip.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.board.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b from Board b where b.isBlocked = false "
            + "and b.isDeleted = false "
            + "and b.isPublic = true "
            + "and b.reportCount < 10 "
            + "order by b.createdAt desc "
            + "limit 20")
    List<Board> findOotdAll(); // 역순

    @Query("SELECT b from Board b where b.id = :boardId "
            + "and b.isBlocked = false "
            + "and b.isDeleted = false "
            + "and b.isPublic = true "
            + "and b.reportCount < 10 ")
    Optional<Board> findOotd(@Param(value = "boardId") Long boardId);

    @Query("SELECT b from Board b where b.id = :boardId "
            + "and b.isBlocked = false "
            + "and b.isDeleted = false "
            + "and b.reportCount < 10 ")
    Optional<Board> findOotdRegardlessOfIsPublic(@Param(value = "boardId") Long boardId);
}
