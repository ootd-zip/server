package zip.ootd.ootdzip.ootd.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.ootd.data.OotdSearchSortType;
import zip.ootd.ootdzip.ootd.domain.Ootd;
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
}
