package zip.ootd.ootdzip.clothes.data;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;
import zip.ootd.ootdzip.clothes.domain.ClothesStyle;

@Data
@Builder
public class ClothesStyleDto {

    private Long id;

    private String name;

    public static ClothesStyleDto createClothesStyleDtoBy(ClothesStyle clothesStyle) {
        return ClothesStyleDto.builder()
                .id(clothesStyle.getId())
                .name(clothesStyle.getStyle().getName())
                .build();
    }

    public static List<ClothesStyleDto> clothesStyleDtosBy(List<ClothesStyle> clothesStyles) {
        return clothesStyles.stream()
                .map(ClothesStyleDto::createClothesStyleDtoBy)
                .collect(Collectors.toList());
    }
}
