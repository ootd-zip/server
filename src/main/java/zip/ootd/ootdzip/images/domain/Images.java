package zip.ootd.ootdzip.images.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String imageUrl = "";

    @Builder.Default
    private String imageUrlBig = "";

    @Builder.Default
    private String imageUrlMedium = "";

    @Builder.Default
    private String imageUrlSmall = "";

    // 패턴 상수화시 재사용되어 성능 향상
    private static final Pattern pattern = Pattern.compile("(https://ootdzip.*?/)(.*?)(\\.(png|jpg|jpeg))");

    public static final Integer LARGE = 800;

    public static final Integer MEDIUM = 400;

    public static final Integer SMALL = 200;

    public static final String FILE_EXTENSION = ".jpg";

    public static Images defaultImage() {
        return Images.builder().build();
    }

    public static Images of(String imageUrl) {

        return Images.builder()
                .imageUrl(imageUrl)
                .imageUrlBig(makeThumbnailUrl(imageUrl, LARGE, LARGE))
                .imageUrlMedium(makeThumbnailUrl(imageUrl, MEDIUM, MEDIUM))
                .imageUrlSmall(makeThumbnailUrl(imageUrl, SMALL, SMALL))
                .build();
    }

    private static String makeThumbnailUrl(String url, Integer width, Integer height) {
        // 정규 표현식 패턴 설정
        Matcher matcher = pattern.matcher(url);

        // 매칭된 부분 추출
        if (!matcher.find()) {
            throw new IllegalArgumentException("잘못된 이미지 URL, 추출 불가");
        }

        String baseUrl = matcher.group(1);
        String fileName = makeResizedFileName(matcher.group(2), width, height);
        String extension = matcher.group(3);

        return baseUrl + fileName + extension;
    }

    public static String makeResizedFileName(String name, int width, int height) {
        return name + "_" + width + "x" + height;
    }

    // 썸네일 이미지 없을 경우 원본 이미지 반환 //
    public String getImageUrlBig() {
        if (imageUrlBig.isBlank()) {
            return imageUrl;
        }

        return imageUrlBig;
    }

    public String getImageUrlMedium() {
        if (imageUrlMedium.isBlank()) {
            return imageUrl;
        }

        return imageUrlMedium;
    }

    public String getImageUrlSmall() {
        if (imageUrlSmall.isBlank()) {
            return imageUrl;
        }

        return imageUrlSmall;
    }
}
