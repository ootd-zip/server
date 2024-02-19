package zip.ootd.ootdzip.clothes.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateClothesIsOpenSvcReq {

    private final Long clothesId;

    private final Boolean isOpen;

    @Builder
    private UpdateClothesIsOpenSvcReq(Long clothesId, Boolean isOpen) {
        this.clothesId = clothesId;
        this.isOpen = isOpen;
    }
}
