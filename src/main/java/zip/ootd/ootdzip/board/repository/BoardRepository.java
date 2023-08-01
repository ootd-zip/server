package zip.ootd.ootdzip.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zip.ootd.ootdzip.board.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
