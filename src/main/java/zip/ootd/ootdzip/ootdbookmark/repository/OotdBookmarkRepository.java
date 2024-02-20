package zip.ootd.ootdzip.ootdbookmark.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.ootdbookmark.domain.OotdBookmark;

@Repository
public interface OotdBookmarkRepository extends JpaRepository<OotdBookmark, Long> {

    @Query("SELECT ob from OotdBookmark ob "
            + "join fetch ob.ootd o "
            + "join fetch ob.user u "
            + "where o.isPrivate = false "
            + "and u.id = :userId ")
    Slice<OotdBookmark> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
