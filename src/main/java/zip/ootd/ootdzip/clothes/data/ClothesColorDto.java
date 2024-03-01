package zip.ootd.ootdzip.clothes.data;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;

@Data
@NoArgsConstructor
public class ClothesColorDto {

    private Long id;

    private String colorCode;

    private String name;

    @Builder
    private ClothesColorDto(Long id, String colorCode, String name) {
        this.id = id;
        this.colorCode = colorCode;
        this.name = name;
    }

    public static ClothesColorDto createClothesColorDtoBy(ClothesColor clothesColor) {
        return ClothesColorDto.builder()
                .id(clothesColor.getColor().getId())
                .colorCode(clothesColor.getColor().getColorCode())
                .name(clothesColor.getColor().getName())
                .build();
    }

    public static List<ClothesColorDto> createClothesColorDtosBy(List<ClothesColor> clothesColors) {
        return clothesColors.stream()
                .map(ClothesColorDto::createClothesColorDtoBy)
                .toList();
    }
}
