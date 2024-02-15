package zip.ootd.ootdzip.brand.controller.request;

import lombok.Setter;
import zip.ootd.ootdzip.brand.service.request.BrandSearchSvcReq;

@Setter
public class BrandSearchReq {

    private String brandName;

    public BrandSearchSvcReq toServiceRequest() {
        return BrandSearchSvcReq.builder()
                .name(this.brandName)
                .build();
    }
}
