package zip.ootd.ootdzip.clothes.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindClothesByUserSvcReq {

    private final Long userId;

    @Builder
    private FindClothesByUserSvcReq(Long userId) {
        this.userId = userId;
    }
}
