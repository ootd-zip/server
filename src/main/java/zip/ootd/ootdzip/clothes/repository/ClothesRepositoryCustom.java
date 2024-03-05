package zip.ootd.ootdzip.clothes.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import zip.ootd.ootdzip.clothes.controller.response.SearchClothesRes;

public interface ClothesRepositoryCustom {
    Slice<SearchClothesRes> searchClothesBy(Long loginUserId,
            Long userId,
            Boolean isPrivate,
            List<Long> brandIds,
            List<Long> categoryIds,
            List<Long> colorIds,
            Pageable pageable);
}
