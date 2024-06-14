package zip.ootd.ootdzip.ootd.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Repository
public interface OotdRepository extends JpaRepository<Ootd, Long>, OotdRepositoryCustom {

    @Query("SELECT o from Ootd o "
            + "where (o.isPrivate = false or o.writer.id = :userId) "
            + "AND o.writer.isDeleted = false "
            + "AND o.writer.id NOT IN :userIds")
    Slice<Ootd> findAllByUserIdAndWriterIdNotIn(@Param("userId") Long userId,
            @Param("userIds") Set<Long> userIds,
            Pageable pageable);

    @Query("SELECT o from Ootd o where o.writer.id = :userId "
            + "and (o.isPrivate = false or o.writer.id = :loginUserId) "
            + "AND o.writer.isDeleted = false ")
    Slice<Ootd> findAllByUserIdAndLoginUserId(@Param("userId") Long userId,
            @Param("loginUserId") Long loginUserId,
            Pageable pageable);

    @Query("SELECT o from Ootd o where o.id in(:ootdIds)")
    Slice<Ootd> findAllByIds(@Param("ootdIds") List<Long> ootdIds, Pageable pageable);

    @Query("SELECT o from Ootd o where o.isPrivate = false "
            + "and o.writer.id = :userId "
            + "and o.id <> :ootdId "
            + "and o.isPrivate = false "
            + "AND o.writer.isDeleted = false ")
    Slice<Ootd> findAllByUserIdAndOotdId(@Param("userId") Long userId,
            @Param("ootdId") Long ootdId,
            Pageable pageable);

    @Query("SELECT o FROM Ootd o "
            + "JOIN o.styles os ON os.style IN (:styles) "
            + "AND o.id <> :ootdId "
            + "AND o.isPrivate = false "
            + "AND o.writer.isDeleted = false "
            + "WHERE o.writer.id NOT IN :userIds ")
    Slice<Ootd> findAllByOotdIdNotAndStylesWriterIdNotIn(
            @Param("ootdId") Long ootdId,
            @Param("styles") List<Style> styles,
            @Param("userIds") Set<Long> userIds,
            Pageable pageable);

    @Query("SELECT o.viewCount FROM Ootd o WHERE o.id = :ootdId")
    Long findViewCountByOotdId(@Param("ootdId") Long ootdId);

    @Modifying // 더티체크를 사용하지 않을 경우, 해당 어노테이션을 이용해 1차캐시를 비워줍니다.
    @Query("UPDATE Ootd o SET o.viewCount = :viewCount WHERE o.id = :ootdId")
    void updateViewCountByOotdId(@Param("ootdId") Long ootdId, @Param("viewCount") Long viewCount);
}
