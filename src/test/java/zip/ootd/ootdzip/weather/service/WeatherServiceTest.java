package zip.ootd.ootdzip.weather.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zip.ootd.ootdzip.weather.data.Temperatures;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @DisplayName("기온 정보를 가져오는 지 테스트한다.")
    @Test
    void testTemperature() {
        Temperatures temperatures = weatherService.getTemperatures(37.338902, 127.096809, LocalDate.now());

        System.out.println(temperatures);
        System.out.println(temperatures.getHighestTemperature());
        System.out.println(temperatures.getLowestTemperature());
        assertThat(temperatures).isNotNull();
    }
}