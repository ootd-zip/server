package zip.ootd.ootdzip.common.util;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TimeUtilTest {
    @DisplayName("댓글 작성시 표기 시간 규칙(지금)을 지키는지 확인한다.")
    @Test
    void compareCreatedTimeAndNowForNow() {
        // given
        LocalDateTime createdAt = LocalDateTime.now();

        // when & then
        assertThat(TimeUtil.compareCreatedTimeAndNow(createdAt)).isEqualTo("지금");
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~분전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1분 전'",
            "30, '30분 전'",
            "59, '59분 전'"
    })
    void compareCreatedTimeAndNowForLastMinute(long minutesAgo, String expected) {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(minutesAgo);

        // when & then
        assertThat(TimeUtil.compareCreatedTimeAndNow(createdAt)).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~시간전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1시간 전'",
            "12, '12시간 전'",
            "23, '23시간 전'"
    })
    void compareCreatedTimeAndNowLastHour(long hoursAgo, String expected) {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusHours(hoursAgo);

        // when & then
        assertThat(TimeUtil.compareCreatedTimeAndNow(createdAt)).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~일전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1일 전'",
            "3, '3일 전'",
            "6, '6일 전'"
    })
    void compareCreatedTimeAndNowForLastDay(long daysAgo, String expected) {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(daysAgo);

        // when & then
        assertThat(TimeUtil.compareCreatedTimeAndNow(createdAt)).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~주전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "7, '1주 전'",
            "10, '1주 전'",
            "15, '2주 전'",
            "27, '3주 전'",
            "28, '4주 전'"
    })
    void compareCreatedTimeAndNowForLastWeek(long daysAgo, String expected) {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(daysAgo);

        // when & then
        assertThat(TimeUtil.compareCreatedTimeAndNow(createdAt)).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~달전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1달 전'",
            "6, '6달 전'",
            "11, '11달 전'"
    })
    void compareCreatedTimeAndNowForLastMonth(long monthsAgo, String expected) {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusMonths(monthsAgo);

        // when & then
        assertThat(TimeUtil.compareCreatedTimeAndNow(createdAt)).isEqualTo(expected);
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~년전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1, '1년 전'",
            "100, '100년 전'"
    })
    void compareCreatedTimeAndNowForLastYear(long yearsAgo, String expected) {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusYears(yearsAgo);

        // when & then
        assertThat(TimeUtil.compareCreatedTimeAndNow(createdAt)).isEqualTo(expected);
    }
}
