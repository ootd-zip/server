package zip.ootd.ootdzip.clothes.data;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.category.data.DetailCategory;
import zip.ootd.ootdzip.category.data.SizeRes;
import zip.ootd.ootdzip.clothes.domain.Clothes;

@Data
@NoArgsConstructor
public class FindClothesRes {

    private Long id;

    private String name;

    private String userName;

    private BrandDto brand;

    private Boolean isPrivate;

    private DetailCategory category;

    private SizeRes size;

    private String memo;

    private String purchaseStore;

    private PurchaseStoreType purchaseStoreType;

    private String purchaseDate;

    private List<ClothesColorDto> colors;

    private String imageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    private FindClothesRes(Long id, String name, String userName, BrandDto brand, Boolean isPrivate,
            DetailCategory category, SizeRes size, String memo, String purchaseStore,
            PurchaseStoreType purchaseStoreType, String purchaseDate, List<ClothesColorDto> colors, String imageUrl,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.brand = brand;
        this.isPrivate = isPrivate;
        this.category = category;
        this.size = size;
        this.memo = memo;
        this.purchaseStore = purchaseStore;
        this.purchaseStoreType = purchaseStoreType;
        this.purchaseDate = purchaseDate;
        this.colors = colors;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static FindClothesRes of(Clothes clothes) {
        return FindClothesRes.builder()
                .id(clothes.getId())
                .name(clothes.getName())
                .userName(clothes.getUser().getName())
                .brand(BrandDto.of(clothes.getBrand()))
                .isPrivate(clothes.getIsPrivate())
                .category(DetailCategory.of(clothes.getCategory()))
                .size(SizeRes.of(clothes.getSize()))
                .memo(clothes.getMemo())
                .purchaseStore(clothes.getPurchaseStore())
                .purchaseStoreType(clothes.getPurchaseStoreType())
                .purchaseDate(clothes.getPurchaseDate())
                .colors(ClothesColorDto.createClothesColorDtosBy(clothes.getClothesColors()))
                .imageUrl(clothes.getImageUrl())
                .createdAt(clothes.getCreatedAt())
                .updatedAt(clothes.getUpdatedAt())
                .build();
    }
}
