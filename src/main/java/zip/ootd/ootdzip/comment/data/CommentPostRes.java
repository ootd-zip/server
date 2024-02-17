package zip.ootd.ootdzip.comment.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.comment.domain.Comment;

@Data
@NoArgsConstructor
public class CommentPostRes {

    private Long id;

    public CommentPostRes(Comment comment) {
        this.id = comment.getId();
    }
}
