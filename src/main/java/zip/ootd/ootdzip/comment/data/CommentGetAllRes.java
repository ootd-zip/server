package zip.ootd.ootdzip.comment.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.common.util.TimeUtil;

@Builder
@AllArgsConstructor
@Data
public class CommentGetAllRes {

    private Long id;

    private Long userId;

    private String userName;

    private String userImage;

    private String content;

    private String timeStamp;

    private String taggedUserName;

    private Integer depth;

    private Long parentId;

    private Long groupId;

    public static CommentGetAllRes of(Comment comment) {

        return CommentGetAllRes.builder()
                .id(comment.getId())
                .userId(comment.getWriter().getId())
                .userName(comment.getWriter().getName())
                .userImage(comment.getWriter().getProfileImage())
                .content(comment.getContents())
                .timeStamp(TimeUtil.compareCreatedTimeAndNow(comment.getCreatedAt()))
                .taggedUserName(comment.getTaggedUserName())
                .depth(comment.getDepth())
                .groupId(comment.getGroupId())
                .parentId(comment.getParentCommentId())
                .build();
    }
}
