package zip.ootd.ootdzip.ootd.data;

import lombok.Data;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Data
public class OotdGetByUserRes {

    private Long id;

    private String image;

    public OotdGetByUserRes(Ootd ootd) {
        this.id = ootd.getId();
        this.image = ootd.getOotdImages().get(0).getImageUrl();
    }
}
