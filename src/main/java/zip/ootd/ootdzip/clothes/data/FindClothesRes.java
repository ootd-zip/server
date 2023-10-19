package zip.ootd.ootdzip.clothes.data;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import zip.ootd.ootdzip.category.data.DetailCategory;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesImage;

@Data
@Builder
public class FindClothesRes {

    private Long id;

    private String name;

    private String userName;

    private String brandName;

    private Boolean isOpen;

    private DetailCategory category;

    private String size;

    private String material;

    private String purchaseStore;

    private String purchaseDate;

    private List<ClothesColorDto> colors;

    private List<ClothesStyleDto> styles;

    private List<String> images;

    public static FindClothesRes createFindClothesRes(Clothes clothes, DetailCategory detailCategory) {
        return FindClothesRes.builder()
                .id(clothes.getId())
                .name(clothes.getName())
                .userName(clothes.getUser().getName())
                .brandName(clothes.getBrand().getName())
                .isOpen(clothes.getIsOpen())
                .category(detailCategory)
                .size(clothes.getSize())
                .material(clothes.getMaterial())
                .purchaseStore(clothes.getPurchaseStore())
                .purchaseDate(clothes.getPurchaseDate())
                .colors(ClothesColorDto.createClothesColorDtosBy(clothes.getClothesColors()))
                .styles(ClothesStyleDto.clothesStyleDtosBy(clothes.getClothesStyles()))
                .images(clothes.getClothesImages().stream().map(ClothesImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}
