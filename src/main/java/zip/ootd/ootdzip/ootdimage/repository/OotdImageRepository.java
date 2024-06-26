package zip.ootd.ootdzip.ootdimage.repository;

import java.util.Set;

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
            + "AND (o.isPrivate = false or o.writer.id = :loginUserId) "
            + "AND o.writer.isDeleted = false "
            + "AND o.writer.id NOT IN :userIds ")
    Page<OotdImage> findByClothesAndUserIdAndLoginUserIdAndWriterIdNotIn(
            @Param("loginUserId") Long loginUserId,
            @Param("clothesId") Long clothesId,
            @Param("userIds") Set<Long> userIds,
            Pageable pageable);
}
