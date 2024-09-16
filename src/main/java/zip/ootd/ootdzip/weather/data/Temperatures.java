package zip.ootd.ootdzip.weather.data;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Temperatures {

    @NotNull
    private Double highestTemperature;

    @NotNull
    private Double lowestTemperature;
}
