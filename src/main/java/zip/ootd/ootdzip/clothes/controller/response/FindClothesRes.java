package zip.ootd.ootdzip.clothes.controller.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.category.data.DetailCategory;
import zip.ootd.ootdzip.category.data.SizeRes;
import zip.ootd.ootdzip.clothes.data.ClothesColorDto;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.domain.Clothes;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FindClothesRes {

    private Long id;

    private String name;

    private Long userId;

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

    public static FindClothesRes of(Clothes clothes) {
        return FindClothesRes.builder()
                .id(clothes.getId())
                .name(clothes.getName())
                .userId(clothes.getUser().getId())
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
                .imageUrl(clothes.getImages().getImageUrl())
                .createdAt(clothes.getCreatedAt())
                .updatedAt(clothes.getUpdatedAt())
                .build();
    }
}
