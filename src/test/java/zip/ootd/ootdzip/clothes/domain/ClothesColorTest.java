package zip.ootd.ootdzip.clothes.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import zip.ootd.ootdzip.category.domain.Color;

class ClothesColorTest {

    @DisplayName("Color 리스트를 받으면 ClothesColor 리스트를 생성한다")
    @Test
    void createClothesColorsBy() {
        // given
        Color color1 = Color.builder()
                .name("색1")
                .colorCode("code1")
                .build();

        Color color2 = Color.builder()
                .name("색2")
                .colorCode("code2")
                .build();

        // when
        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(List.of(color1, color2));

        //then
        assertThat(clothesColors).hasSize(2)
                .extracting("color.name", "color.colorCode")
                .containsAnyOf(
                        tuple("색1", "code1"),
                        tuple("색2", "code2")
                );

    }
}