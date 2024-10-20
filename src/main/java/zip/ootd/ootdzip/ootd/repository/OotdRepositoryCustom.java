package zip.ootd.ootdzip.ootd.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.ootd.data.OotdSearchSortType;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;

public interface OotdRepositoryCustom {

    CommonPageResponse<Ootd> searchOotds(String searchText,
            List<Long> brandIds,
            List<Long> categoryIds,
            List<Long> colorIds,
            UserGender writerGender,
            Set<Long> nonAccessibleUserIds,
            OotdSearchSortType sortType,
            Pageable pageable);

    List<Ootd> findOotdToday(double highestTemp, double lowestTemp, User user);

    /**
     * 현재 사용자의 옷 중 clothesList의 옷과 색상 1개 이상 및 카테고리가 일치하는 옷을 찾는다.
     * @param clothesList 찾을 옷 목록.
     * @param user 현재 사용자.
     * @return 찾을 옷 목록의 옷을 key로, 일치하는 현재 사용자의 옷들을 value로 가지는 Map.
     */
    Map<Clothes, Set<Clothes>> findMatchingUserClothes(List<Clothes> clothesList, User user);
}
