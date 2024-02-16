package zip.ootd.ootdzip.ootd.data;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OotdPutReq {

    @NotNull
    private Long id;

    @Size(max = 3000, message = "메모는 최대 3000자 입니다.")
    private String content;

    @NotNull
    private Boolean isPrivate;

    private List<Long> styles;

    @Valid
    @NotEmpty(message = "OOTD 게시글에는 반드시 1장 이상의 이미지가 있어야 합니다.")
    private List<OotdImageReq> ootdImages;

    @Data
    public static class OotdImageReq {

        @NotEmpty(message = "OOTD 이미지 URL 는 반드시 존재해야 합니다.")
        private String ootdImage;

        private List<ClothesTagReq> clothesTags;

        @Data
        public static class ClothesTagReq {

            private Long clothesId;

            private String xRate;

            private String yRate;

            private Long deviceWidth;

            private Long deviceHeight;
        }
    }
}
