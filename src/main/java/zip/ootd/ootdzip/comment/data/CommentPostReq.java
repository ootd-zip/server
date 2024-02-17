package zip.ootd.ootdzip.comment.data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentPostReq {

    @NotNull(message = "OOTD id 값은 필수 입니다.")
    private Long ootdId;

    private String taggedUserName;

    private Long commentParentId;

    @NotNull(message = "부모댓글인지 자식댓글인지 값을 알려주어야 합니다.")
    @Max(1)
    @Min(0)
    private Integer parentDepth;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 3000, message = "댓글은 최대 3000자 입니다.")
    private String content;
}
