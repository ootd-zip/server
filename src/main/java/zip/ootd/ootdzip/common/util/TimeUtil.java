package zip.ootd.ootdzip.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtil {

    /**
     * 1분 미만 : 지금
     * 60분 미만 : 몇분전
     * 24시간 미만 : 몇시간전
     * 일주일 미만 : 몇일전
     * 한달 미만 : 몇주전
     * 일년 미만 : 몇개월전
     * 일년 이상 : 몇년전
     */
    public static String compareCreatedTimeAndNow(LocalDateTime createdAt) {
        LocalDateTime createdTimeLT = createdAt;
        LocalDateTime nowLT = LocalDateTime.now();

        LocalDate createdTimeLD = createdAt.toLocalDate();
        LocalDate nowLD = LocalDateTime.now().toLocalDate();

        long seconds = ChronoUnit.SECONDS.between(createdTimeLT, nowLT);
        if (seconds < 60) {
            return "지금";
        } else if (seconds < 3600) { // 3600 = 1시간
            long minutes = ChronoUnit.MINUTES.between(createdTimeLT, nowLT);
            return minutes + "분전";
        } else if (seconds < 86400) { //86400 = 1일
            long hours = ChronoUnit.HOURS.between(createdTimeLT, nowLT);
            return hours + "시간전";
        } else if (seconds < 604800) { //604800 = 1주일
            long days = ChronoUnit.DAYS.between(createdTimeLD, nowLD);
            return days + "일전";
        } else if (seconds < 3024000 && ChronoUnit.MONTHS.between(createdTimeLD, nowLD) == 0) { //3024000 = 35일
            long weeks = ChronoUnit.WEEKS.between(createdTimeLD, nowLD);
            return weeks + "주전";
        } else if (seconds < 31536000 && ChronoUnit.YEARS.between(createdTimeLD, nowLD) == 0) {
            // 31536000 = 365일, 1년이 366일때는 대비해 년도 비교 추가
            long months = ChronoUnit.MONTHS.between(createdTimeLD, nowLD);
            return months + "달전";
        } else {
            long years = ChronoUnit.YEARS.between(createdTimeLD, nowLD);
            return years + "년전";
        }
    }
}
