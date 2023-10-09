package zip.ootd.ootdzip.clothes.data;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;

@Data
@Builder
public class ClothesColorDto {

    private Long id;

    private String name;

    public static ClothesColorDto createClothesColorDtoBy(ClothesColor clothesColor) {
        return ClothesColorDto.builder()
                .id(clothesColor.getId())
                .name(clothesColor.getColor().getName())
                .build();
    }

    public static List<ClothesColorDto> createClothesColorDtosBy(List<ClothesColor> clothesColors) {
        return clothesColors.stream()
                .map(ClothesColorDto::createClothesColorDtoBy)
                .collect(Collectors.toList());
    }
}
