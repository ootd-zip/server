package zip.ootd.ootdzip.images.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Images {

    @Builder.Default
    @Column(nullable = false)
    private String image = "";

    @Builder.Default
    private String image173x173 = "";

    @Builder.Default
    private String image70x70 = "";

    @Builder.Default
    private String image32x32 = "";

    public static Images of(String image) {
        return Images.builder()
                .image(image)
                .image173x173(makeThumbnailUrl(image, 173, 173))
                .image70x70(makeThumbnailUrl(image, 70, 70))
                .image32x32(makeThumbnailUrl(image, 32, 32))
                .build();
    }

    private static String makeThumbnailUrl(String url, Integer width, Integer height) {
        // 정규 표현식 패턴 설정
        String regex = "(https://ootdzip.*?/)(.*?)(\\.png)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        // 매칭된 부분 추출
        if (!matcher.find()) {
            throw new IllegalArgumentException("잘못된 이미지 URL, 추출 불가");
        }

        String baseUrl = matcher.group(1);
        String fileName = matcher.group(2) + "_" + width + "x" + height;
        String extension = matcher.group(3);

        return baseUrl + fileName + extension;
    }
}
