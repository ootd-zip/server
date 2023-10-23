package zip.ootd.ootdzip.ootd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.ootd.domain.Ootd;

public interface OotdRepository extends JpaRepository<Ootd, Long> {

    @Query("SELECT o from Ootd o where o.isBlocked = false "
            + "and o.isDeleted = false "
            + "and o.isPublic = true "
            + "and o.reportCount < 10 "
            + "order by o.createdAt desc "
            + "limit 20")
    List<Ootd> findOotdAll(); // 역순

    @Query("SELECT o from Ootd o where o.id = :ootdId "
            + "and o.isBlocked = false "
            + "and o.isDeleted = false "
            + "and o.isPublic = true "
            + "and o.reportCount < 10 ")
    Optional<Ootd> findOotd(@Param(value = "ootdId") Long ootdId);

    @Query("SELECT o from Ootd o where o.id = :ootdId "
            + "and o.isBlocked = false "
            + "and o.isDeleted = false "
            + "and o.reportCount < 10 ")
    Optional<Ootd> findOotdRegardlessOfIsPublic(@Param(value = "ootdId") Long ootdId);
}
