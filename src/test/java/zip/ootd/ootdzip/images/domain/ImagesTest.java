package zip.ootd.ootdzip.images.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import zip.ootd.ootdzip.common.exception.CustomException;

public class ImagesTest {

    @DisplayName("이미지 링크를 분리 합니다")
    @Test
    void makeImageUrl() {

        // given
        String url = "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14.png";

        // when
        Images images = Images.of(url);

        // then
        assertThat(images.getImageUrlBig())
                .isEqualTo("https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14_800x800.png");
        assertThat(images.getImageUrlMedium())
                .isEqualTo("https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14_400x400.png");
        assertThat(images.getImageUrlSmall())
                .isEqualTo("https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14_200x200.png");
    }

    @DisplayName("이미지 URL을 입력하면 예외를 반환하지 않는다.")
    @Test
    void checkValidImageUrl() {
        // given
        String imageUrl = "image.png";

        // when & then
        Images.checkValidImageUrl(imageUrl);
    }

    @DisplayName("이미지 확장자가 아닌 다른 URL을 입력하면 예외를 반환한다")
    @Test
    void checkValidImageUrlWithInvalidImageUrl() {
        // given
        String invalidImageUrl = "image.exe";

        // when & then
        assertThrows(CustomException.class, () -> Images.checkValidImageUrl(invalidImageUrl));
    }
}
