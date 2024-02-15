package zip.ootd.ootdzip.brand.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BrandSearchSvcReq {

    private final String name;

    @Builder
    private BrandSearchSvcReq(String name) {
        this.name = name;
    }
}
