package zip.ootd.ootdzip.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zip.ootd.ootdzip.board.domain.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b from Board b where b.isBlocked = false " +
            "and b.isDeleted = false " +
            "and b.isPublic = true " +
            "and b.reportCount < 10 " +
            "order by b.createdAt desc " +
            "limit 20")
    List<Board> findOotdAll(); // 역순
}
