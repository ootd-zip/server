package zip.ootd.ootdzip.comment.repository;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import zip.ootd.ootdzip.comment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT COALESCE(MAX(c.groupId), 0) from Comment c where c.ootd.id = :ootdId")
    Long findMaxGroupIdByOotdId(@Param("ootdId") Long ootdId);

    @Query("SELECT COALESCE(MAX(c.groupOrder), 0) from Comment c where c.ootd.id = :ootdId and c.groupId = :groupId")
    Long findMaxGroupIdByOotdIdAndGroupOrder(@Param("ootdId") Long ootdId, @Param("groupId") Long groupId);

    @Query("SELECT c from Comment c where "
            + "c.ootd.id = :ootdId "
            + "AND ((c.depth = 2 OR (c.depth = 1 AND c.childCount = 0)) AND c.writer.id NOT IN :userIds"
            + " OR c.depth = 1 AND 0 < c.childCount)")
    Slice<Comment> findAllByOotdId(@Param("ootdId") Long ootdId, @Param("userIds") Set<Long> userIds,
            Pageable pageable);
}

