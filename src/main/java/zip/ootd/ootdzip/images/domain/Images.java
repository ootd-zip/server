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
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Images {

    @Builder.Default
    @Column(nullable = false)
    private String imageUrl = "";

    @Builder.Default
    private String imageUrl173x173 = "";

    @Builder.Default
    private String imageUrl70x70 = "";

    @Builder.Default
    private String imageUrl32x32 = "";

    public static Images of(String imageUrl) {
        return Images.builder()
                .imageUrl(imageUrl)
                .imageUrl173x173(makeThumbnailUrl(imageUrl, 173, 173))
                .imageUrl70x70(makeThumbnailUrl(imageUrl, 70, 70))
                .imageUrl32x32(makeThumbnailUrl(imageUrl, 32, 32))
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

    // 썸네일 이미지 없을 경우 원본 이미지 반환 //

    public String getImageUrl173x173() {
        if (imageUrl173x173.isBlank()) {
            return imageUrl;
        }

        return imageUrl173x173;
    }

    public String getImageUrl70x70() {
        if (imageUrl70x70.isBlank()) {
            return imageUrl;
        }

        return imageUrl70x70;
    }

    public String getImageUrl32x32() {
        if (imageUrl32x32.isBlank()) {
            return imageUrl;
        }

        return imageUrl32x32;
    }
}
