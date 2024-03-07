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
            "1, '1분전'",
            "30, '30분전'",
            "59, '59분전'"
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
            "1, '1시간전'",
            "12, '12시간전'",
            "23, '23시간전'"
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
            "1, '1일전'",
            "3, '3일전'",
            "6, '6일전'"
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
            "7, '1주전'",
            "10, '1주전'",
            "15, '2주전'",
            "27, '3주전'",
            "28, '4주전'"
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
            "1, '1달전'",
            "6, '6달전'",
            "11, '11달전'"
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
            "1, '1년전'",
            "100, '100년전'"
    })
    void compareCreatedTimeAndNowForLastYear(long yearsAgo, String expected) {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusYears(yearsAgo);

        // when & then
        assertThat(TimeUtil.compareCreatedTimeAndNow(createdAt)).isEqualTo(expected);
    }
}
