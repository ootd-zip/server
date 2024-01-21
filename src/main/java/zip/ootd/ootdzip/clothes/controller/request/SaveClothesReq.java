package zip.ootd.ootdzip.clothes.controller.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.clothes.service.request.SaveClothesSvcReq;

@Getter
@NoArgsConstructor
public class SaveClothesReq {

    @NotBlank
    private String purchaseStore;

    @NotNull
    @Positive
    private Long brandId;

    @NotNull
    @Positive
    private Long categoryId;

    private List<@Positive Long> colorIds;

    @NotNull
    private Boolean isOpen;

    @NotNull
    private Long sizeId;

    @NotBlank
    private String clothesImageUrl;

    @NotBlank
    private String name;

    private String material;

    private String purchaseDate;

    @Builder
    private SaveClothesReq(String purchaseStore, Long brandId, Long categoryId, List<Long> colorIds,
            Boolean isOpen, Long sizeId, String clothesImageUrl, String name, String material, String purchaseDate) {
        this.purchaseStore = purchaseStore;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.colorIds = colorIds;
        this.isOpen = isOpen;
        this.sizeId = sizeId;
        this.clothesImageUrl = clothesImageUrl;
        this.name = name;
        this.material = material;
        this.purchaseDate = purchaseDate;
    }

    public SaveClothesSvcReq toServiceRequest() {
        return SaveClothesSvcReq.builder()
                .purchaseStore(this.purchaseStore)
                .brandId(this.brandId)
                .categoryId(this.categoryId)
                .colorIds(this.colorIds)
                .isOpen(this.isOpen)
                .sizeId(this.sizeId)
                .clothesImageUrl(this.clothesImageUrl)
                .material(this.material)
                .name(this.name)
                .purchaseDate(this.purchaseDate)
                .build();
    }
}
