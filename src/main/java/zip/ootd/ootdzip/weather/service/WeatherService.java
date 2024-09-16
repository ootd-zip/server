package zip.ootd.ootdzip.weather.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zip.ootd.ootdzip.weather.client.WeatherClient;
import zip.ootd.ootdzip.weather.data.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherClient weatherClient;

    @Value("${api.weather.key}")
    private String serviceKey;

    public Temperatures getTemperatures(double lat, double lng, LocalDate date) {
        Grid grid = GpsTransfer.transfer(lat, lng);
        WeatherRequest weatherRequest = WeatherRequest.of(serviceKey, date, grid.getX(), grid.getY());
        Map<String, Object> weatherRequestQuery = convertWeatherRequestToMap(weatherRequest);
        WeatherResponse weatherResponse = weatherClient.getWeather(weatherRequestQuery);
        List<ForecastItem> items = weatherResponse.getItems();

        List<Double> temperatures = items.stream()
            .filter(item -> item.getForecastDateTime().toLocalDate().isEqual(date))
            .filter(item -> item.getCategory().equals("TMP"))
            .map(ForecastItem::getFcstValueAsDouble)
            .toList();
        return Temperatures.builder()
            .highestTemperature(temperatures.stream().max(Double::compare).orElseThrow())
            .lowestTemperature(temperatures.stream().min(Double::compare).orElseThrow())
            .build();

    }

    private Map<String, Object> convertWeatherRequestToMap(WeatherRequest weatherRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(weatherRequest, new TypeReference<>() {
        });
    }

}
