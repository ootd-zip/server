package zip.ootd.ootdzip.home.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SameClothesDifferentFeelRes {

    private Long clothesId;
    private String clothesName;
    private CategoryForSameClothesDifferentFeelRes clothesCategory;
    private List<ColorForSameClothesDifferentFeelRes> clothesColors;
    private List<OotdImageForSameClothesDifferentFeelRes> ootds;

    public SameClothesDifferentFeelRes(Clothes clothes, List<Ootd> ootds) {
        this.clothesId = clothes.getId();
        this.clothesName = clothes.getName();
        this.clothesCategory = CategoryForSameClothesDifferentFeelRes.of(clothes.getCategory());
        this.clothesColors = ColorForSameClothesDifferentFeelRes.of(clothes.getClothesColors());
        this.ootds = OotdImageForSameClothesDifferentFeelRes.of(ootds);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    static class CategoryForSameClothesDifferentFeelRes {
        private Long id;
        private String categoryName;

        public static CategoryForSameClothesDifferentFeelRes of(Category category) {
            return CategoryForSameClothesDifferentFeelRes.builder()
                    .id(category.getId())
                    .categoryName(category.getName())
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    static class ColorForSameClothesDifferentFeelRes {
        private Long id;

        public static List<ColorForSameClothesDifferentFeelRes> of(List<ClothesColor> clothesColors) {
            return clothesColors.stream()
                    .map(x -> ColorForSameClothesDifferentFeelRes.builder()
                            .id(x.getColor().getId())
                            .build())
                    .toList();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    static class OotdImageForSameClothesDifferentFeelRes {

        private Long ootdId;

        private String imageUrl;

        private Integer imageCount;

        public static List<OotdImageForSameClothesDifferentFeelRes> of(List<Ootd> ootds) {
            return ootds.stream()
                    .map(x -> OotdImageForSameClothesDifferentFeelRes.builder()
                            .ootdId(x.getId())
                            .imageUrl(x.getFirstImage())
                            .imageCount(x.getImageCount())
                            .build())
                    .toList();
        }
    }
}
