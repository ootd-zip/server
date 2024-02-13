package zip.ootd.ootdzip.clothes.data;

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

    private Boolean isOpen;

    private DetailCategory category;

    private SizeRes size;

    private String memo;

    private String purchaseStore;

    private String purchaseDate;

    private List<ClothesColorDto> colors;

    private String imageUrl;

    @Builder
    private FindClothesRes(Long id, String name, String userName, BrandDto brand, Boolean isOpen,
            DetailCategory category, SizeRes size, String memo, String purchaseStore, String purchaseDate,
            List<ClothesColorDto> colors, String imageUrl) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.brand = brand;
        this.isOpen = isOpen;
        this.category = category;
        this.size = size;
        this.memo = memo;
        this.purchaseStore = purchaseStore;
        this.purchaseDate = purchaseDate;
        this.colors = colors;
        this.imageUrl = imageUrl;
    }

    public static FindClothesRes of(Clothes clothes) {
        return FindClothesRes.builder()
                .id(clothes.getId())
                .name(clothes.getName())
                .userName(clothes.getUser().getName())
                .brand(new BrandDto(clothes.getBrand()))
                .isOpen(clothes.getIsOpen())
                .category(DetailCategory.builder()
                        .id(clothes.getCategory().getId())
                        .categoryName(clothes.getCategory().getName())
                        .parentCategoryName(clothes.getCategory().getParentCategory().getName())
                        .build())
                .size(SizeRes.of(clothes.getSize()))
                .memo(clothes.getMemo())
                .purchaseStore(clothes.getPurchaseStore())
                .purchaseDate(clothes.getPurchaseDate())
                .colors(ClothesColorDto.createClothesColorDtosBy(clothes.getClothesColors()))
                .imageUrl(clothes.getImageUrl())
                .build();
    }
}
