package zip.ootd.ootdzip.ootdimage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.ootdimage.domain.OotdImage;

@Repository
public interface OotdImageRepository extends JpaRepository<OotdImage, Long>, OotdImageRepositoryCustom {

    @Query("SELECT DISTINCT oi FROM OotdImage oi "
            + "JOIN FETCH oi.ootd o "
            + "JOIN oi.ootdImageClothesList oc "
            + "JOIN oc.clothes c ON c.id = :clothesId "
            + "WHERE o.writer.id = c.user.id "
            + "AND (o.isPrivate = false or o.writer.id = :loginUserId) ")
    Page<OotdImage> findByClothesAndUserIdAndLoginUserId(
            @Param("loginUserId") Long loginUserId,
            @Param("clothesId") Long clothesId,
            Pageable pageable);
}
