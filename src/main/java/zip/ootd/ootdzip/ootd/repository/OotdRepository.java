package zip.ootd.ootdzip.ootd.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.ootd.domain.Ootd;

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

    // Long countByWriterAndOotdClothesListIsNull(User writer);
    //
    // List<Ootd> findByWriterAndOotdClothesListIsNull(User writer, Pageable pageable);

    /**
     * 주의 : 영속화의 경우 Ootd, ootdClothesList 만 됐으므로 그 이외는 조회만 사용할것
     * TODO : 내옷, 신고, 차단, 삭제 필터링
     */
    @Query("SELECT distinct o from Ootd o "
            + "join fetch o.ootdClothesList oc "
            + "join oc.clothes c on c.category = :category "
            + "join c.clothesColors cc on cc.color.name in (:colorNames)")
    List<Ootd> findByClothesColorNamesAndClothesCategory(
            @Param("colorNames") List<String> colorNames,
            @Param("category") Category category,
            Pageable pageable);
}
