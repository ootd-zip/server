package zip.ootd.ootdzip.ootdimage.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.user.domain.User;

public interface OotdImageRepositoryCustom {

    List<OotdImage> findOotdImageForSCDF(List<Long> colorIds, Category category, User user, Pageable pageable);
}
