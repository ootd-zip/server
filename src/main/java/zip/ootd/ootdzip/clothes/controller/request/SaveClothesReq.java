package zip.ootd.ootdzip.clothes.controller.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.clothes.service.request.SaveClothesSvcReq;

@Getter
@RequiredArgsConstructor
public class SaveClothesReq {

    @NotBlank(message = "구매처는 필수입니다.")
    private String purchaseStore;

    @Positive(message = "브랜드 ID는 양수여야 합니다.")
    private Long brandId;

    @Positive(message = "카테고리 ID는 양수여야 합니다.")
    private Long categoryId;

    @NotEmpty(message = "색은 필수입니다.")
    private List<@Positive(message = "색 ID는 양수여야 합니다.") Long> colorIds;

    @NotNull(message = "공개여부는 필수입니다.")
    private Boolean isOpen;

    @Positive(message = "사이즈 ID는 양수여야 합니다.")
    private Long sizeId;

    @NotBlank(message = "이미지는 필수입니다.")
    private String clothesImageUrl;

    @NotBlank(message = "제품명은 필수입니다.")
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
