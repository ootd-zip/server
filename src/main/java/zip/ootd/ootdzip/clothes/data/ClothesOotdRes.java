package zip.ootd.ootdzip.clothes.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ClothesOotdRes {

    private Long id;

    private Integer isTagged;

    private String imageUrl;

    private String clothesName;

    private String brandName;

    private String categoryName;

    private String sizeName;

    public static ClothesOotdRes of(ClothesOotdRepoRes clothesOotdRepoRes) {
        return ClothesOotdRes.builder()
                .id(clothesOotdRepoRes.getId())
                .isTagged(clothesOotdRepoRes.getIsTagged())
                .imageUrl(clothesOotdRepoRes.getImageUrl())
                .clothesName(clothesOotdRepoRes.getClothesName())
                .brandName(clothesOotdRepoRes.getBrandName())
                .categoryName(clothesOotdRepoRes.getCategoryName())
                .sizeName(clothesOotdRepoRes.getSizeName())
                .build();
    }
}
