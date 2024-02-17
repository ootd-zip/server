package zip.ootd.ootdzip.comment.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CommentTest {

    @DisplayName("댓글 작성시 표기 시간 규칙(지금)을 지키는지 확인한다.")
    @Test
    void compareCreatedTimeAndNowForNow() {
        // given
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now());

        // when & then
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo("지금");
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~분전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1분전'",
            "30, '30분전'",
            "59, '59분전'"
    })
    void compareCreatedTimeAndNowForLastMinute(long minutesAgo, String expected) {
        // given
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now().minusMinutes(minutesAgo));

        // when & then
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~시간전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1시간전'",
            "12, '12시간전'",
            "23, '23시간전'"
    })
    void compareCreatedTimeAndNowLastHour(long hoursAgo, String expected) {
        // given
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now().minusHours(hoursAgo));

        // when & then
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~일전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1일전'",
            "3, '3일전'",
            "6, '6일전'"
    })
    void compareCreatedTimeAndNowForLastDay(long daysAgo, String expected) {
        // given
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now().minusDays(daysAgo));

        // when & then
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~주전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "7, '1주전'",
            "10, '1주전'",
            "15, '2주전'",
            "27, '3주전'",
            "28, '4주전'"
    })
    void compareCreatedTimeAndNowForLastWeek(long daysAgo, String expected) {
        // given
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now().minusDays(daysAgo));

        // when & then
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~달전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1달전'",
            "6, '6달전'",
            "11, '11달전'"
    })
    void compareCreatedTimeAndNowForLastMonth(long monthsAgo, String expected) {
        // given
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now().minusMonths(monthsAgo));

        // when & then
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~년전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1년전'",
            "100, '100년전'"
    })
    void compareCreatedTimeAndNowForLastYear(long yearsAgo, String expected) {
        // given
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now().minusYears(yearsAgo));

        // when & then
        assertThat(comment.compareCreatedTimeAndNow()).isEqualTo(expected);
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
