package zip.ootd.ootdzip.comment.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Data
public class CommentGetAllReq extends CommonPageRequest {

    @NotNull(message = "댓글 조회시 어떤 ootd 인지 id 를 알려주어야 합니다.")
    public Long ootdId;
}
