package zip.ootd.ootdzip.ootdbookmark.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.ootdbookmark.domain.OotdBookmark;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public interface OotdBookmarkRepository extends JpaRepository<OotdBookmark, Long> {

    @Query("SELECT ob from OotdBookmark ob "
            + "join fetch ob.ootd o "
            + "join fetch ob.user u "
            + "where (o.isPrivate = false or o.writer.id = :userId) "
            + "and u.id = :userId "
            + "AND o.writer.isDeleted = false "
            + "AND o.writer.id NOT IN :userIds ")
    Page<OotdBookmark> findAllByUserIdAndWriterIdNotIn(@Param("userId") Long userId,
            @Param("userIds") Set<Long> userIds,
            Pageable pageable);

    List<OotdBookmark> findAllByUserAndIdIn(User user, List<Long> ids);
}
