package zip.ootd.ootdzip.ootdbookmark.data;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OotdBookmarkDeleteReq {

    @NotEmpty(message = "삭제할 ootdBookmark id 는 필수입니다.")
    private List<Long> ootdBookmarkIds;
}
