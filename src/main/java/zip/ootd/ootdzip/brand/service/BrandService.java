package zip.ootd.ootdzip.brand.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.brand.service.request.BrandSearchSvcReq;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<BrandDto> getBrands(BrandSearchSvcReq request) {
        List<Brand> brands = brandRepository.findByNameStartsWithOrEngNameStartsWith(request.getName(),
                request.getName().toUpperCase(),
                Sort.by(Sort.Direction.ASC, "name"));

        return brands
                .stream()
                .map(BrandDto::of)
                .toList();

    }

    public List<BrandDto> getUserBrands(Long userId, User loginUser) {
        Boolean isPrivate = null;

        if (!userId.equals(loginUser.getId())) {
            isPrivate = false;
        }

        List<Brand> userBrands = brandRepository.getUserBrands(userId, isPrivate);

        return userBrands.stream().map(BrandDto::of).toList();
    }
}
