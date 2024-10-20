package zip.ootd.ootdzip.weather.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

import zip.ootd.ootdzip.weather.data.WeatherResponse;

@FeignClient(name = "WeatherClient", url = "${api.weather.url}")
public interface WeatherClient {

    @GetMapping(consumes = "application/json", path = "?{weatherRequest}")
    WeatherResponse getWeather(@SpringQueryMap Map<String, Object> weatherRequest);
}
