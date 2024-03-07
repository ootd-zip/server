package zip.ootd.ootdzip.notification.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class NotificationTest {

    @DisplayName("알람 표기 시간 규칙(오늘 : 24시간)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1",
            "12",
            "23"
    })
    void compareSimpleCreatedTimeAndNowForToday(long hoursAgo) {
        // given
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now().minusHours(hoursAgo));

        // when & then
        assertThat(notification.compareSimpleCreatedTimeAndNow()).isEqualTo("오늘");
    }

    @DisplayName("알람 표기 시간 규칙(어제 : 48시간)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "24",
            "36",
            "47"
    })
    void compareSimpleCreatedTimeAndNowForYesterday(long hoursAgo) {
        // given
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now().minusHours(hoursAgo));

        // when & then
        assertThat(notification.compareSimpleCreatedTimeAndNow()).isEqualTo("어제");
    }

    @DisplayName("알람 표기 시간 규칙(일주일 24 * 7)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "48",
            "108",
            "167"
    })
    void compareSimpleCreatedTimeAndNowForWeek(long hoursAgo) {
        // given
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now().minusHours(hoursAgo));

        // when & then
        assertThat(notification.compareSimpleCreatedTimeAndNow()).isEqualTo("최근 일주일");
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(한 달)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "7",
            "10",
            "15",
            "27"
    })
    void compareCreatedTimeAndNowForLastWeek(long daysAgo) {
        // given
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now().minusDays(daysAgo));

        // when & then
        assertThat(notification.compareSimpleCreatedTimeAndNow()).isEqualTo("최근 한 달");
    }

    @DisplayName("댓글 작성시 표기 시간 규칙(~달전)을 지키는지 확인한다.")
    @ParameterizedTest
    @CsvSource({
            "1",
            "6",
            "11",
            "12",
            "24"
    })
    void compareCreatedTimeAndNowForLastMonth(long monthsAgo) {
        // given
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now().minusMonths(monthsAgo));

        // when & then
        assertThat(notification.compareSimpleCreatedTimeAndNow()).isEqualTo("오래 전");
    }
}
