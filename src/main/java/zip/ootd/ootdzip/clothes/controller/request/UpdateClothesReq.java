package zip.ootd.ootdzip.clothes.controller.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesSvcReq;

@Getter
@NoArgsConstructor
public class UpdateClothesReq {

    private String purchaseStore;

    private PurchaseStoreType purchaseStoreType;

    private Long brandId;

    private Long categoryId;

    private List<Long> colorIds;

    private Boolean isOpen;

    private Long sizeId;

    private String clothesImageUrl;

    private String memo;

    private String name;

    private String purchaseDate;

    @Builder
    public UpdateClothesReq(String purchaseStore, PurchaseStoreType purchaseStoreType, Long brandId, Long categoryId,
            List<Long> colorIds, Boolean isOpen, Long sizeId, String clothesImageUrl, String memo, String name,
            String purchaseDate) {
        this.purchaseStore = purchaseStore;
        this.purchaseStoreType = purchaseStoreType;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.colorIds = colorIds;
        this.isOpen = isOpen;
        this.sizeId = sizeId;
        this.clothesImageUrl = clothesImageUrl;
        this.memo = memo;
        this.name = name;
        this.purchaseDate = purchaseDate;
    }

    public UpdateClothesSvcReq toServiceRequest(Long clothesId) {
        return UpdateClothesSvcReq.builder()
                .clothesId(clothesId)
                .purchaseStore(this.purchaseStore)
                .purchaseStoreType(this.purchaseStoreType)
                .brandId(this.brandId)
                .categoryId(this.categoryId)
                .colorIds(this.colorIds)
                .isOpen(this.isOpen)
                .sizeId(this.sizeId)
                .clothesImageUrl(this.clothesImageUrl)
                .memo(this.memo)
                .name(this.name)
                .purchaseDate(this.purchaseDate)
                .build();
    }
}
