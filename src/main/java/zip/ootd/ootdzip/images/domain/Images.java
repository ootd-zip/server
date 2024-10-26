package zip.ootd.ootdzip.images.domain;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;

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

    private static final List<String> imageExtensions = List.of("jpg", "jpeg", "png");

    public static Images defaultImage() {
        return Images.builder().build();
    }

    public static Images of(String imageUrl) {

        checkValidImageUrl(imageUrl);
        return Images.builder()
                .imageUrl(imageUrl)
                .imageUrlBig(makeThumbnailUrl(imageUrl, LARGE, LARGE))
                .imageUrlMedium(makeThumbnailUrl(imageUrl, MEDIUM, MEDIUM))
                .imageUrlSmall(makeThumbnailUrl(imageUrl, SMALL, SMALL))
                .build();
    }

    public static String makeThumbnailUrl(String url, Integer width, Integer height) {
        Matcher matcher = pattern.matcher(url);

        // 매칭된 부분 추출
        if (!matcher.find()) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
        }

        String baseUrl = matcher.group(1);
        String fileName = makeResizedFileName(matcher.group(2), width, height);
        String extension = matcher.group(3);

        return baseUrl + fileName + extension;
    }

    public static String makeResizedFileName(String name, int width, int height) {
        return name + "_" + width + "x" + height;
    }

    // 이미지 링크로 부터 원본 이름 얻기
    public static String getNameFromImageUrl(String url) {
        Matcher matcher = pattern.matcher(url);

        // 매칭된 부분 추출
        if (!matcher.find()) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
        }

        return matcher.group(2);
    }

    // imageUrl 이 이미지 링크인지 체경
    public static void checkValidImageUrl(String imageUrl) {
        String extension = FilenameUtils.getExtension(imageUrl);
        if (!imageExtensions.contains(extension.toLowerCase())) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
        }
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

    // imageUrl, imageUrlBig, imageMedium, imageUrlSmall 값이 같으면 모두 같은 객체로 인식 합니다.
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Images images = (Images)object;

        if (!imageUrl.equals(images.imageUrl)) {
            return false;
        }
        if (!imageUrlBig.equals(images.getImageUrlBig())) {
            return false;
        }
        if (!imageUrlMedium.equals(images.getImageUrlMedium())) {
            return false;
        }
        return imageUrlSmall.equals(images.getImageUrlSmall());
    }

    @Override
    public int hashCode() {
        int result = imageUrl.hashCode();
        result = 31 * result + getImageUrlBig().hashCode();
        result = 31 * result + getImageUrlMedium().hashCode();
        result = 31 * result + getImageUrlSmall().hashCode();
        return result;
    }
}
