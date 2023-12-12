package zip.ootd.ootdzip.brand.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brand.repository.BrandRepository;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

}
