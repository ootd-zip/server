package zip.ootd.ootdzip.ootdbookmark.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.ootdbookmark.domain.OotdBookmark;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public interface OotdBookmarkRepository extends JpaRepository<OotdBookmark, Long> {

    @Query("SELECT ob from OotdBookmark ob "
            + "join fetch ob.ootd o "
            + "join fetch ob.user u "
            + "where (o.isPrivate = false or o.writer.id = :userId) "
            + "and u.id = :userId "
            + "AND o.writer.isDeleted = false ")
    Page<OotdBookmark> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    List<OotdBookmark> findAllByUserAndIdIn(User user, List<Long> ids);
}
