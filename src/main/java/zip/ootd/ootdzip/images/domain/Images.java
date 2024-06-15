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

    public static Images defaultImage() {
        return Images.builder().build();
    }

    public static Images of(String imageUrl) {

        Integer largeUrl = 800;
        Integer mediumUrl = 400;
        Integer smallUrl = 200;

        return Images.builder()
                .imageUrl(imageUrl)
                .imageUrlBig(makeThumbnailUrl(imageUrl, largeUrl, largeUrl))
                .imageUrlMedium(makeThumbnailUrl(imageUrl, mediumUrl, mediumUrl))
                .imageUrlSmall(makeThumbnailUrl(imageUrl, smallUrl, smallUrl))
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
