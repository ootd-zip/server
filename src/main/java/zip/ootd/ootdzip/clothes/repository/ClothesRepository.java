package zip.ootd.ootdzip.clothes.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public interface ClothesRepository extends JpaRepository<Clothes, Long> {

    List<Clothes> findByUser(User user);

    List<Clothes> findByUserAndIsOpenTrue(User user);

    @Query("SELECT COUNT(c) "
            + "FROM Clothes c "
            + "WHERE c.user = :user "
            + "AND (c.size IS NULL OR c.memo IS NULL OR c.purchaseStore IS NULL OR c.purchaseDate IS NULL)")
    Long countByUserAndNoDetailInfo(@Param("user") User user);

    @Query("SELECT c "
            + "FROM Clothes c "
            + "WHERE c.user = :user "
            + "AND (c.size IS NULL OR c.memo IS NULL OR c.purchaseStore IS NULL OR c.purchaseDate IS NULL)")
    List<Clothes> findByUserAndNoDetailInfo(@Param("user") User user, Pageable pageable);

    @Query("SELECT COUNT(c) "
            + "FROM Clothes  c "
            + "WHERE FUNCTION('DATE', c.createdAt) = :targetDate "
            + "AND c.user = :user ")
    Long countByDate(@Param("targetDate") LocalDate targetDate, @Param("user") User user);

    @Query("SELECT c "
            + "FROM Clothes  c "
            + "WHERE FUNCTION('DATE', c.createdAt) = :targetDate "
            + "AND c.user = :user ")
    List<Clothes> findByDate(@Param("targetDate") LocalDate targetDate, @Param("user") User user, Pageable pageable);

    @Query("SELECT c FROM Clothes c "
            + "LEFT JOIN c.clothesColors cc "
            + "WHERE EXISTS (SELECT so FROM Ootd so "
            + "LEFT JOIN so.ootdImages soi "
            + "LEFT JOIN soi.ootdImageClothesList soc "
            + "LEFT JOIN soc.clothes sc "
            + "LEFT JOIN sc.clothesColors scc "
            + "WHERE sc.category = c.category "
            + "AND scc.color IN (cc.color) "
            + "AND so.writer <> :user "
            + "AND so.isDeleted = false "
            + "AND so.isPrivate = false "
            + "AND so.reportCount < 10)")
    Slice<Clothes> findExistOotd(@Param("user") User user, Pageable pageable);
}
