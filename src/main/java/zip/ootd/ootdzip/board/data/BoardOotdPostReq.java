package zip.ootd.ootdzip.board.data;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import zip.ootd.ootdzip.user.domain.UserGender;

@Data
public class BoardOotdPostReq {

    private String content;

    private Boolean isPublic;

    private UserGender gender;

    private List<Long> styles;

    private List<Long> clotheIds;

    @NotEmpty(message = "이미지는 반드시 1장 이상이여야 합니다.")
    private List<String> ootdImages;
}
