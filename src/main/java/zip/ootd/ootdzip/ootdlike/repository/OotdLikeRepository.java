package zip.ootd.ootdzip.ootdlike.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.ootdlike.domain.OotdLike;

@Repository
public interface OotdLikeRepository extends JpaRepository<OotdLike, Long> {

    @Query(value = "SELECT ol FROM OotdLike ol "
            + "INNER JOIN ol.ootd o "
            + "INNER JOIN ol.user u "
            + "WHERE u.id = :userId "
            + "AND o.isPrivate = false "
            + "AND o.writer.isDeleted = false "
            + "AND o.writer.id NOT IN :userIds ")
    List<OotdLike> findTop10ByUserAndWriterIdNotIn(@Param("userId") Long userId,
            @Param("userIds") Set<Long> userIds,
            Pageable pageable);
}
