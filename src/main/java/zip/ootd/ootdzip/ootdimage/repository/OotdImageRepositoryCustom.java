package zip.ootd.ootdzip.ootdimage.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.user.domain.User;

public interface OotdImageRepositoryCustom {

    List<Ootd> findOotdsFromOotdImageForSCDF(List<Long> colorIds,
            Category category,
            User user,
            Set<Long> nonAccessibleUserIds,
            Pageable pageable);
}
