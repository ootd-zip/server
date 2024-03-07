package zip.ootd.ootdzip.comment.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommentTest {

    @DisplayName("댓글 조회시 삭제여부에 따라 내용이 변한다.")
    @Test
    void getContentWithIsDeleted() {
        // given
        Comment comment = new Comment();
        comment.setContents("안녕하세요");
        comment.setIsDeleted(true);

        // when & then
        assertThat(comment.getContents()).isEqualTo("삭제된 댓글입니다.");
    }

    @DisplayName("댓글 조회시 신고 수에 따라 내용이 변한다.")
    @Test
    void getContentWithReportCount() {
        // given
        Comment comment = new Comment();
        comment.setContents("안녕하세요");
        comment.setReportCount(5);

        // when & then
        assertThat(comment.getContents()).isEqualTo("삭제된 댓글입니다.");
    }
}
