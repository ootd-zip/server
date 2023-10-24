package zip.ootd.ootdzip.ootd.data;

import lombok.Data;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Data
public class OotdPostRes {

    private Long id;

    public OotdPostRes(Ootd ootd) {
        this.id = ootd.getId();
    }
}
