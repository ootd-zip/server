package zip.ootd.ootdzip.home.data;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Data
public class SameClothesDifferentFeelRes {

    private ClothesForSameClothesDifferentFeelRes clothes;

    private List<OotdForSameClothesDifferentFeelRes> ootds;

    public SameClothesDifferentFeelRes(Clothes clothes, List<Ootd> ootds) {
        this.clothes = new ClothesForSameClothesDifferentFeelRes(clothes);
        this.ootds = ootds.stream()
                .map(OotdForSameClothesDifferentFeelRes::new)
                .collect(Collectors.toList());
    }

    @Data
    static class ClothesForSameClothesDifferentFeelRes {

        private Long id;

        private String name;

        private Category category;

        private String imageUrl;

        public ClothesForSameClothesDifferentFeelRes(Clothes clothes) {
            this.id = clothes.getId();
            this.name = clothes.getName();
            this.category = clothes.getCategory();
            this.imageUrl =
                    clothes.getClothesImages().isEmpty() ? "" : clothes.getClothesImages().get(0).getImageUrl();
        }
    }

    @Data
    static class OotdForSameClothesDifferentFeelRes {

        private Long id;

        private String imageUrl;

        public OotdForSameClothesDifferentFeelRes(Ootd ootd) {
            this.id = ootd.getId();
            this.imageUrl = ootd.getOotdImages().isEmpty() ? "" : ootd.getOotdImages().get(0).getImageUrl();
        }
    }
}
