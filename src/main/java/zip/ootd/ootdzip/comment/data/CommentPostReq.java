package zip.ootd.ootdzip.comment.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentPostReq {

    @NotNull
    private Long ootdId;

    private String taggedUserName;

    private Long commentParentId;

    @NotNull(message = "부모댓글인지 자식댓글인지 값을 알려주어야 합니다.")
    private int parentDepth;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
