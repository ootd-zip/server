package zip.ootd.ootdzip.ootdimageclothe.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coordinate {

    private String x;

    private String y;

    public Coordinate(String x, String y) {
        this.x = x;
        this.y = y;
    }
}
