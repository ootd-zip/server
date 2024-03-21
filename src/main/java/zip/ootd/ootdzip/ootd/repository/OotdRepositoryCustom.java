package zip.ootd.ootdzip.ootd.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import zip.ootd.ootdzip.ootd.data.OotdSearchSortType;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.user.domain.UserGender;

public interface OotdRepositoryCustom {

    Slice<Ootd> searchOotds(String searchText,
            List<Long> brandIds,
            List<Long> categoryIds,
            List<Long> colorIds,
            UserGender writerGender,
            OotdSearchSortType sortType,
            Pageable pageable);
}
