package zip.ootd.ootdzip.brand.repository;

import java.util.List;

import zip.ootd.ootdzip.brand.domain.Brand;

public interface BrandRepositoryCustom {
    List<Brand> getUserBrands(Long userId,
            Boolean isPrivate);
}
