package zip.ootd.ootdzip.ootdimageclothe.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coordinate {

    private String xRate;

    private String yRate;

    public Coordinate(String xRate, String yRate) {
        this.xRate = xRate;
        this.yRate = yRate;
    }
}
