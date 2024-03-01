package zip.ootd.ootdzip.clothes.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateClothesIsPrivateSvcReq {

    private final Long clothesId;

    private final Boolean isPrivate;

    @Builder
    private UpdateClothesIsPrivateSvcReq(Long clothesId, Boolean isPrivate) {
        this.clothesId = clothesId;
        this.isPrivate = isPrivate;
    }
}
