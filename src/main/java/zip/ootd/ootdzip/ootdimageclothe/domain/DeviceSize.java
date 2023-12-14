package zip.ootd.ootdzip.ootdimageclothe.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceSize {

    private Long deviceWidth;

    private Long deviceHeight;

    public DeviceSize(Long deviceWidth, Long deviceHeight) {
        this.deviceWidth = deviceWidth;
        this.deviceHeight = deviceHeight;
    }
}
