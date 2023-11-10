package zip.ootd.ootdzip.ootd.data;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import zip.ootd.ootdzip.user.domain.UserGender;

@Data
public class OotdPostReq {

    private String content;

    private Boolean isPrivate;

    private UserGender gender;

    private List<Long> styles;

    private List<OotdImageReq> ootdImages;

    @Data
    public static class OotdImageReq {

        @NotEmpty(message = "이미지는 반드시 1장 이상이여야 합니다.")
        private String ootdImage;

        private List<ClothesTagReq> clothesTags;

        @Data
        public static class ClothesTagReq {

            private Long clothesId;

            private String xRate;

            private String yRate;

            private Long deviceWeight;

            private Long deviceHeight;
        }
    }
}
