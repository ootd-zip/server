package zip.ootd.ootdzip.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImageFileUtilTest {

    @DisplayName("이미지 URL을 입력하면 true를 반환한다")
    @Test
    void isValidImageUrl() {
        // given
        String imageUrl = "image.png";

        // when
        boolean result = ImageFileUtil.isValidImageUrl(imageUrl);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("이미지 확장자가 아닌 다른 URL을 입력하면 false를 반환한다")
    @Test
    void isValidImageUrlWithInvalidImageUrl() {
        // given
        String invalidImageUrl = "image.exe";

        // when
        boolean result = ImageFileUtil.isValidImageUrl(invalidImageUrl);

        //then
        assertThat(result).isFalse();
    }
}
