package zip.ootd.ootdzip.ootd.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.ootd.domain.Ootd;

public interface OotdRepository extends JpaRepository<Ootd, Long> {

    @Query("SELECT o from Ootd o where (o.isPrivate = false or o.writer.id = :userId) ")
    Slice<Ootd> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o from Ootd o where o.id in(:ootdIds)")
    Slice<Ootd> findAllByIds(@Param("ootdIds") List<Long> ootdIds, Pageable pageable);

    @Query("SELECT o from Ootd o where o.isPrivate = false "
            + "and o.writer.id = :userId "
            + "and o.id <> :ootdId")
    Slice<Ootd> findAllByUserIdAndOotdId(@Param("userId") Long userId,
            @Param("ootdId") Long ootdId,
            Pageable pageable);
}
