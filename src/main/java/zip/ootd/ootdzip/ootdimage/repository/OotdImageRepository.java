package zip.ootd.ootdzip.ootdimage.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.user.domain.User;

public interface OotdImageRepository extends JpaRepository<OotdImage, Long> {

    @Query("SELECT DISTINCT oi FROM OotdImage oi "
            + "JOIN FETCH oi.ootd o "
            + "JOIN oi.ootdImageClothesList oc "
            + "JOIN oc.clothes c ON c.category = :category "
            + "JOIN c.clothesColors cc ON cc.color.name IN (:colorNames) "
            + "AND o.writer <> :user "
            + "ORDER BY o.viewCount DESC")
    List<OotdImage> findByClothesColorNamesAndClothesCategory(
            @Param("colorNames") List<String> colorNames,
            @Param("category") Category category,
            @Param("user") User user,
            Pageable pageable);

    @Query("SELECT DISTINCT oi FROM OotdImage oi "
            + "JOIN FETCH oi.ootd o "
            + "JOIN o.styles os ON os.style IN (:styles) "
            + "AND o.id <> :ootdId "
            + "AND o.isPrivate = false ")
    Slice<OotdImage> findByStyles(
            @Param("ootdId") Long ootdId,
            @Param("styles") List<Style> styles,
            Pageable pageable);

    @Query("SELECT DISTINCT oi FROM OotdImage oi "
            + "JOIN FETCH oi.ootd o "
            + "JOIN oi.ootdImageClothesList oc "
            + "JOIN oc.clothes c ON c.id = :clothesId "
            + "WHERE o.writer.id = c.user.id "
            + "AND (o.isPrivate = false or o.writer.id = :loginUserId) ")
    Slice<OotdImage> findByClothesAndUserIdAndLoginUserId(
            @Param("loginUserId") Long loginUserId,
            @Param("clothesId") Long clothesId,
            Pageable pageable);
}