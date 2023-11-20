package zip.ootd.ootdzip.home.data;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import zip.ootd.ootdzip.category.data.CategoryType;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;

@Data
public class SameClothesDifferentFeelRes {

    private ClothesForSameClothesDifferentFeelRes clothes;

    private List<OotdImageForSameClothesDifferentFeelRes> ootdImages;

    public SameClothesDifferentFeelRes(Clothes clothes, List<OotdImage> ootdImages) {
        this.clothes = new ClothesForSameClothesDifferentFeelRes(clothes);
        this.ootdImages = ootdImages.stream()
                .map(OotdImageForSameClothesDifferentFeelRes::new)
                .collect(Collectors.toList());
    }

    @Data
    static class ClothesForSameClothesDifferentFeelRes {

        private Long id;

        private String name;

        private CategoryType categoryType;

        private String imageUrl;

        public ClothesForSameClothesDifferentFeelRes(Clothes clothes) {
            this.id = clothes.getId();
            this.name = clothes.getName();
            this.categoryType = clothes.getCategory().getType();
            this.imageUrl =
                    clothes.getClothesImages().isEmpty() ? "" : clothes.getClothesImages().get(0).getImageUrl();
        }
    }

    @Data
    static class OotdImageForSameClothesDifferentFeelRes {

        private Long ootdId;

        private String imageUrl;

        public OotdImageForSameClothesDifferentFeelRes(OotdImage ootdImage) {
            this.ootdId = ootdImage.getOotd().getId();
            this.imageUrl = ootdImage.getImageUrl();
        }
    }
}
