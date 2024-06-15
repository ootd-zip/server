package zip.ootd.ootdzip.images.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ImagesTest {

    @DisplayName("이미지 링크를 분리 합니다")
    @Test
    void makeImageUrl() {

        // given
        String url = "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14.png";

        // when
        Images images = Images.of(url);

        // then
        assertThat(images.getImageUrl173x173())
                .isEqualTo("https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14_173x173.png");
        assertThat(images.getImageUrl70x70())
                .isEqualTo("https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14_70x70.png");
        assertThat(images.getImageUrl32x32())
                .isEqualTo("https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14_32x32.png");
    }
}
