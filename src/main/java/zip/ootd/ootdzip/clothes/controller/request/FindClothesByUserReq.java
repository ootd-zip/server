package zip.ootd.ootdzip.clothes.controller.request;

import lombok.Data;
import zip.ootd.ootdzip.clothes.service.request.FindClothesByUserSvcReq;

@Data
public class FindClothesByUserReq {

    private Long userId;

    public FindClothesByUserSvcReq toServiceRequest() {
        return FindClothesByUserSvcReq.builder()
                .userId(this.userId)
                .build();
    }
}
