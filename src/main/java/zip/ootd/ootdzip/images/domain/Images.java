package zip.ootd.ootdzip.images.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Images {

    @Column(nullable = false)
    private String image;

    private String image173x173;

    private String image70x70;

    private String image32x32;

    public Images(String image) {

        this.image = image;
        this.image173x173 = makeThumbnailUrl(image, 173, 173);
        this.image70x70 = makeThumbnailUrl(image, 70, 70);
        this.image32x32 = makeThumbnailUrl(image, 32, 32);
    }

    private String makeThumbnailUrl(String url, Integer width, Integer height) {
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
