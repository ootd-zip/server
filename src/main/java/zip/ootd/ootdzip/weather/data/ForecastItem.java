package zip.ootd.ootdzip.weather.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;

@Getter
public class ForecastItem {

    private String baseDate;
    private String baseTime;
    private String category;
    private String fcstDate;
    private String fcstTime;
    private String fcstValue;
    private Integer nx;
    private Integer ny;

    public LocalDateTime getForecastDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmm");
        return LocalDateTime.parse(fcstDate + fcstTime, formatter);
    }

    public double getFcstValueAsDouble() {
        return Double.parseDouble(fcstValue);
    }
}
