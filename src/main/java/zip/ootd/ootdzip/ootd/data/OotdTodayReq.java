package zip.ootd.ootdzip.ootd.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OotdTodayReq {

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;
}
