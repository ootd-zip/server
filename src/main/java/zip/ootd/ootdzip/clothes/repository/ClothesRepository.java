package zip.ootd.ootdzip.clothes.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.clothes.data.ClothesOotdRepoRes;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public interface ClothesRepository extends JpaRepository<Clothes, Long>, ClothesRepositoryCustom {

    List<Clothes> findByUser(User user, Pageable pageable);

    List<Clothes> findByUserAndIsPrivateFalse(User user, Pageable pageable);

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

    @Query("SELECT DISTINCT c FROM Clothes c "
            + "JOIN c.ootdImageClothesList oic "
            + "JOIN oic.ootdImage oi "
            + "JOIN oi.ootd o ON o.id = :ootdId "
            + "WHERE c.isPrivate = false ")
    List<Clothes> findByOotdId(@Param("ootdId") Long ootdId);

    @Query(value =
            "SELECT c.id, c.created_at AS CREATEAT, 1 AS ISTAGGED, c.image_url_big AS imageUrl, "
                    + "c.name AS clothesName, b.name AS brandName, cg.name AS categoryName, s.name AS sizeName "
                    + "FROM clothes c "
                    + "JOIN brands b ON c.brand_id = b.id "
                    + "JOIN categories cg ON c.category_id = cg.id "
                    + "JOIN sizes s ON c.size_id = s.id "
                    + "JOIN users u ON c.user_id = u.id AND u.is_deleted = false "
                    + "WHERE c.is_private = false "
                    + "AND c.id IN :clothesIds "
                    + "AND c.user_id = :userId "
                    + "UNION ALL "
                    + "SELECT c.id, c.created_at AS CREATEAT, 0 AS ISTAGGED, c.image_url AS imageUrl, "
                    + "c.name AS clothesName, b.name AS brandName, cg.name AS categoryName, s.name AS sizeName "
                    + "FROM clothes c "
                    + "JOIN brands b ON c.brand_id = b.id "
                    + "JOIN categories cg ON c.category_id = cg.id "
                    + "JOIN sizes s ON c.size_id = s.id "
                    + "JOIN users u ON c.user_id = u.id AND u.is_deleted = false "
                    + "WHERE c.is_private = false "
                    + "AND c.id NOT IN :clothesIds "
                    + "AND c.user_id = :userId "
                    + "ORDER BY ISTAGGED DESC, CREATEAT DESC "
                    // + "LIMIT :size OFFSET :page ",
                    + "OFFSET :page ROWS FETCH FIRST :size ROWS ONLY ",
            nativeQuery = true)
    Slice<ClothesOotdRepoRes> findClothesOotdResByOotdId(@Param("userId") Long userId,
            @Param("clothesIds") List<Long> clothesIds,
            @Param("page") Integer page,
            @Param("size") Integer size);
}
