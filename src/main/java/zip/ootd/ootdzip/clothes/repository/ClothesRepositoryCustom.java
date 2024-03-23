package zip.ootd.ootdzip.clothes.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import zip.ootd.ootdzip.clothes.controller.response.FindClothesRes;

public interface ClothesRepositoryCustom {
    Slice<FindClothesRes> searchClothesBy(Long loginUserId,
            Long userId,
            Boolean isPrivate,
            List<Long> brandIds,
            List<Long> categoryIds,
            List<Long> colorIds,
            String searchText,
            Pageable pageable);
}
