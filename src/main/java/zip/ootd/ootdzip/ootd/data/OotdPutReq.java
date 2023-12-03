package zip.ootd.ootdzip.ootd.data;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OotdPutReq {

    private String content;

    private Boolean isPrivate;

    private List<Long> styles;

    @Size(min = 1, message = "OOTD 게시글에는 반드시 1장 이상의 이미지가 있어야 합니다.")
    private List<OotdImageReq> ootdImages;

    @Data
    public static class OotdImageReq {

        @NotEmpty(message = "OOTD 이미지는 반드시 존재해야 합니다.")
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
