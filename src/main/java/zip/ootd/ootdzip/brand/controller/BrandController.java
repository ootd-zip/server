package zip.ootd.ootdzip.brand.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brand.service.BrandService;

@RestController
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
}
