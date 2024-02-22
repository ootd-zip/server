package zip.ootd.ootdzip.comment.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import zip.ootd.ootdzip.comment.domain.Comment;

@Builder
@AllArgsConstructor
@Data
public class CommentGetAllRes {

    private Long id;

    private String userName;

    private String userImage;

    private String content;

    private String timeStamp;

    private String taggedUserName;

    private int depth;

    private Long parentId;

    private Long groupId;

    private Long groupOrder;

    public static CommentGetAllRes of(Comment comment) {

        return CommentGetAllRes.builder()
                .id(comment.getId())
                .userName(comment.getWriter().getName())
                .userImage(comment.getWriter().getProfileImage())
                .content(comment.getContents())
                .timeStamp(comment.compareCreatedTimeAndNow())
                .taggedUserName(comment.getTaggedUser() == null ? null : comment.getTaggedUser().getName())
                .depth(comment.getDepth())
                .groupId(comment.getGroupId())
                .groupOrder(comment.getGroupOrder())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .build();
    }
}
