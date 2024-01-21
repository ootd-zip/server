package zip.ootd.ootdzip.clothes.service.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SaveClothesSvcReq {

    private final String purchaseStore;

    private final Long brandId;

    private final Long categoryId;

    private final List<Long> colorIds;

    private final Boolean isOpen;

    private final Long sizeId;

    private final String clothesImageUrl;

    private final String material;

    private final String name;

    private final String purchaseDate;

    @Builder
    private SaveClothesSvcReq(String purchaseStore, Long brandId, Long categoryId, List<Long> colorIds, Boolean isOpen,
            Long sizeId, String clothesImageUrl, String material, String name, String purchaseDate) {
        this.purchaseStore = purchaseStore;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.colorIds = colorIds;
        this.isOpen = isOpen;
        this.sizeId = sizeId;
        this.clothesImageUrl = clothesImageUrl;
        this.material = material;
        this.name = name;
        this.purchaseDate = purchaseDate;
    }
}
