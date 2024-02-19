package zip.ootd.ootdzip.clothes.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesIsOpenSvcReq;

@Getter
@NoArgsConstructor
public class UpdateClothesIsOpenReq {

    @NotNull(message = "공개여부는 필수입니다.")
    private Boolean isOpen;

    @Builder
    private UpdateClothesIsOpenReq(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public UpdateClothesIsOpenSvcReq toServiceRequest(Long clothesId) {
        return UpdateClothesIsOpenSvcReq.builder()
                .clothesId(clothesId)
                .isOpen(this.isOpen)
                .build();
    }
}
