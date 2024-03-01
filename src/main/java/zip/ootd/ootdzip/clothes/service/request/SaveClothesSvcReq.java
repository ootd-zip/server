package zip.ootd.ootdzip.clothes.service.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;

@Getter
public class SaveClothesSvcReq {

    private final String purchaseStore;

    private final PurchaseStoreType purchaseStoreType;

    private final Long brandId;

    private final Long categoryId;

    private final List<Long> colorIds;

    private final Boolean isPrivate;

    private final Long sizeId;

    private final String clothesImageUrl;

    private final String memo;

    private final String name;

    private final String purchaseDate;

    @Builder
    private SaveClothesSvcReq(String purchaseStore, PurchaseStoreType purchaseStoreType, Long brandId, Long categoryId,
            List<Long> colorIds, Boolean isPrivate, Long sizeId, String clothesImageUrl, String memo, String name,
            String purchaseDate) {
        this.purchaseStore = purchaseStore;
        this.purchaseStoreType = purchaseStoreType;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.colorIds = colorIds;
        this.isPrivate = isPrivate;
        this.sizeId = sizeId;
        this.clothesImageUrl = clothesImageUrl;
        this.memo = memo;
        this.name = name;
        this.purchaseDate = purchaseDate;
    }
}
