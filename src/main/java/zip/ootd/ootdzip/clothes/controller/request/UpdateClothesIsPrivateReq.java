package zip.ootd.ootdzip.clothes.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesIsPrivateSvcReq;

@Getter
@NoArgsConstructor
public class UpdateClothesIsPrivateReq {

    @NotNull(message = "공개여부는 필수입니다.")
    private Boolean isPrivate;

    @Builder
    private UpdateClothesIsPrivateReq(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public UpdateClothesIsPrivateSvcReq toServiceRequest(Long clothesId) {
        return UpdateClothesIsPrivateSvcReq.builder()
                .clothesId(clothesId)
                .isPrivate(this.isPrivate)
                .build();
    }
}
