package zip.ootd.ootdzip.ootd.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Data
@NoArgsConstructor
public class OotdGetSimilarRes {

    private Long id;

    private String image;

    private Integer imageCount;

    public OotdGetSimilarRes(Ootd ootd) {
        this.id = ootd.getId();
        this.image = ootd.getFirstImage();
        this.imageCount = ootd.getImageCount();
    }
}
