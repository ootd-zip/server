package zip.ootd.ootdzip.comment.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommentTest {

    @DisplayName("댓글 작성시 표기 시간 규칙을 지키는지 확인한다.")
    @Test
    void compareCreatedTimeAndNow() {
        // given
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now());

        // when & then
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("지금");

        comment.setCreatedAt(LocalDateTime.now().minusMinutes(1L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("1분전");
        comment.setCreatedAt(LocalDateTime.now().minusMinutes(30L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("30분전");
        comment.setCreatedAt(LocalDateTime.now().minusMinutes(59L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("59분전");

        comment.setCreatedAt(LocalDateTime.now().minusHours(1L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("1시간전");
        comment.setCreatedAt(LocalDateTime.now().minusHours(12L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("12시간전");
        comment.setCreatedAt(LocalDateTime.now().minusHours(23L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("23시간전");

        comment.setCreatedAt(LocalDateTime.now().minusDays(1L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("1일전");
        comment.setCreatedAt(LocalDateTime.now().minusDays(3L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("3일전");
        comment.setCreatedAt(LocalDateTime.now().minusDays(6L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("6일전");

        comment.setCreatedAt(LocalDateTime.now().minusDays(7L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("1주전");
        comment.setCreatedAt(LocalDateTime.now().minusDays(10L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("1주전");
        comment.setCreatedAt(LocalDateTime.now().minusDays(15L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("2주전");
        comment.setCreatedAt(LocalDateTime.now().minusDays(27L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("3주전");

        comment.setCreatedAt(LocalDateTime.now().minusDays(32L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("1달전");
        comment.setCreatedAt(LocalDateTime.now().minusMonths(6L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("6달전");
        comment.setCreatedAt(LocalDateTime.now().minusMonths(11L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("11달전");

        comment.setCreatedAt(LocalDateTime.now().minusYears(1L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("1년전");
        comment.setCreatedAt(LocalDateTime.now().minusYears(100L));
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("100년전");
    }

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
