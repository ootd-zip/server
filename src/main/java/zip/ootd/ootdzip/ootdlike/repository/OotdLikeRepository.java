package zip.ootd.ootdzip.ootdlike.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
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
            + "AND o.isPrivate = false ")
    List<OotdLike> findTop10ByUser(@Param("userId") Long userId, Sort sort);
}
