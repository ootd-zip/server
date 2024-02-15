package zip.ootd.ootdzip.brand.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.brand.data.BrandSaveReq;
import zip.ootd.ootdzip.brand.service.request.BrandSearchSvcReq;

class BrandServiceTest extends IntegrationTestSupport {

    @Autowired
    private BrandService brandService;

    @DisplayName("브랜드를 저장한다.")
    @Test
    void saveBrand() {
        // given
        BrandSaveReq request = BrandSaveReq.builder()
                .name("브랜드1")
                .build();

        // when
        BrandDto result = brandService.saveBrand(request);

        //then
        assertThat(result.getName()).isEqualTo(request.getName());
    }

    @DisplayName("브랜드를 조회한다.")
    @Test
    void getBrand() {
        // given
        BrandSaveReq request1 = BrandSaveReq.builder()
                .name("브랜드1")
                .build();

        BrandDto brand1 = brandService.saveBrand(request1);

        BrandSaveReq request2 = BrandSaveReq.builder()
                .name("테스트1")
                .build();

        BrandDto brand2 = brandService.saveBrand(request2);

        BrandSearchSvcReq request3 = BrandSearchSvcReq.builder()
                .name("브")
                .build();

        // when
        List<BrandDto> brands = brandService.getBrands(request3);

        //then
        assertThat(brands).hasSize(1)
                .extracting("name")
                .contains("브랜드1");

    }

}
