package zip.ootd.ootdzip.ootd.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Repository
public interface OotdRepository extends JpaRepository<Ootd, Long>, OotdRepositoryCustom {

    @Query("SELECT o from Ootd o where (o.isPrivate = false or o.writer.id = :userId) AND o.writer.isDeleted = false ")
    Slice<Ootd> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

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
            + "AND o.writer.isDeleted = false ")
    Slice<Ootd> findAllByOotdIdNotAndStyles(
            @Param("ootdId") Long ootdId,
            @Param("styles") List<Style> styles,
            Pageable pageable);
}
