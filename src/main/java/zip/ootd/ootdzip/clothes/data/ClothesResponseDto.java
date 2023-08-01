package zip.ootd.ootdzip.clothes.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.category.data.DetailCategoryDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClothesResponseDto {
    private String clothesName;
    private BrandDto brand;
    private DetailCategoryDto category;
    private List<String> styleList;
    private List<String> colorList;
    private List<String> imageList;
    private Boolean isOpen;
    private String size;
    private String material;
    private String purchaseStore;
    private String purchaseDate;
}
