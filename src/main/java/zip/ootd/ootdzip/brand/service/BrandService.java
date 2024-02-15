package zip.ootd.ootdzip.brand.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.brand.data.BrandSaveReq;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.brand.service.request.BrandSearchSvcReq;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional
    public BrandDto saveBrand(BrandSaveReq request) {

        if (request.getName().isBlank()) {
            throw new CustomException(ErrorCode.DUPLICATE_BRAND_NAME);
        }

        if (brandRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_BRAND_NAME);
        }

        Brand brand = Brand.builder().name(request.getName()).build();

        Brand saveBrand = brandRepository.save(brand);

        return BrandDto.of(brand);
    }

    public List<BrandDto> getBrands(BrandSearchSvcReq request) {
        List<Brand> brands = brandRepository.findByNameStartsWith(request.getName(),
                Sort.by(Sort.Direction.ASC, "name"));

        return brands
                .stream()
                .map(BrandDto::of)
                .toList();

    }
}
