package zip.ootd.ootdzip.clothes.data;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.category.data.DetailCategory;
import zip.ootd.ootdzip.category.data.SizeRes;
import zip.ootd.ootdzip.clothes.domain.Clothes;

@Data
@Builder
public class FindClothesRes {

    private Long id;

    private String name;

    private String userName;

    private BrandDto brand;

    private Boolean isOpen;

    private DetailCategory category;

    private SizeRes size;

    private String material;

    private String purchaseStore;

    private String purchaseDate;

    private List<ClothesColorDto> colors;

    private String imageUrl;

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
                .material(clothes.getMaterial())
                .purchaseStore(clothes.getPurchaseStore())
                .purchaseDate(clothes.getPurchaseDate())
                .colors(ClothesColorDto.createClothesColorDtosBy(clothes.getClothesColors()))
                .imageUrl(clothes.getImageUrl())
                .build();
    }
}
