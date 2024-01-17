package zip.ootd.ootdzip.clothes.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClothesImageTest {

    @DisplayName("이미지 URL 리스트를 입력받으면 ClotheImage 리스트를 만든다")
    @Test
    void createClothesImagesBy() {
        // given
        List<String> imageUrls = List.of("image1.jpg", "image2.png");

        // when
        List<ClothesImage> clothesImages = ClothesImage.createClothesImagesBy(imageUrls);

        //then
        assertThat(clothesImages).hasSize(2)
                .extracting("imageUrl")
                .containsExactlyInAnyOrder(
                        "image1.jpg",
                        "image2.png"
                );
    }

    @DisplayName("이미지가 아닌 URL이 포함되어 있는 리스트를 받으면 실패한다.")
    @Test
    void createClothesImagesByInvalidImageUrl() {
        // given
        List<String> imageUrls = List.of("image1.exe", "image2.png");

        // when & then
        assertThatThrownBy(() -> ClothesImage.createClothesImagesBy(imageUrls))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하는 이미지 확장자가 아닙니다.");
    }

}