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

    @NotNull
    private int parentDepth;

    @NotBlank
    private String content;
}
