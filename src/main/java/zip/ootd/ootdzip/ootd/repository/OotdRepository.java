package zip.ootd.ootdzip.ootd.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.user.domain.User;

public interface OotdRepository extends JpaRepository<Ootd, Long> {

    @Query("SELECT o from Ootd o where o.isBlocked = false "
            + "and o.isDeleted = false "
            + "and (o.isPrivate = false or o.writer.id = :userId) "
            + "and o.reportCount < 10 "
            + "order by o.createdAt desc "
            + "limit 20")
    List<Ootd> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT o from Ootd o where o.id in(:ootdIds)")
    Slice<Ootd> findAllByIds(@Param("ootdIds") List<Long> ootdIds, Pageable pageable);
    
    Long countByWriterAndOotdClothesListIsNull(User writer);

    List<Ootd> findByWriterAndOotdClothesListIsNull(User writer, Pageable pageable);
}
