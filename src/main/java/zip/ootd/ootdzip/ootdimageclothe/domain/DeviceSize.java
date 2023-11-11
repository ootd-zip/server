package zip.ootd.ootdzip.ootdimageclothe.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceSize {

    private Long deviceWeight;

    private Long deviceHeight;

    public DeviceSize(Long deviceWeight, Long deviceHeight) {
        this.deviceWeight = deviceWeight;
        this.deviceHeight = deviceHeight;
    }
}
