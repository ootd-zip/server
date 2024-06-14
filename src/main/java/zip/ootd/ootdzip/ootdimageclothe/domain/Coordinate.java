package zip.ootd.ootdzip.ootdimageclothe.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coordinate {

    // stylecheck 때문에 x -> xRate 로 변경, xRate 사용시 해당부분 캐싱할때 맵핑이 안되서 에러 발생할 수 있으니 캐싱사용시주의
    private String xRate;

    private String yRate;

    public Coordinate(String xRate, String yRate) {
        this.xRate = xRate;
        this.yRate = yRate;
    }
}
