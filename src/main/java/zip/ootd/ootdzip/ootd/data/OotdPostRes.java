package zip.ootd.ootdzip.ootd.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Data
@NoArgsConstructor
public class OotdPostRes {

    private Long id;

    public OotdPostRes(Ootd ootd) {
        this.id = ootd.getId();
    }
}
