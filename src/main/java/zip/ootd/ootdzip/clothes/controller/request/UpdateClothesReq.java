package zip.ootd.ootdzip.clothes.controller.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.service.request.UpdateClothesSvcReq;
import zip.ootd.ootdzip.common.valid.EnumValid;

@Getter
@NoArgsConstructor
public class UpdateClothesReq {

    @NotBlank(message = "구매처는 필수입니다.")
    private String purchaseStore;

    @EnumValid(enumClass = PurchaseStoreType.class, message = "유효하지 않은 구매처 타입입니다.")
    private PurchaseStoreType purchaseStoreType;

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

    @Size(max = 2001, message = "메모는 최대 2000자입니다.")
    private String memo;

    @NotBlank(message = "제품명은 필수입니다.")
    private String name;

    private String purchaseDate;

    @Builder
    private UpdateClothesReq(String purchaseStore, PurchaseStoreType purchaseStoreType, Long brandId, Long categoryId,
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
