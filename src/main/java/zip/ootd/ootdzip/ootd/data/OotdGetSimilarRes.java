package zip.ootd.ootdzip.ootd.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;

@Data
@NoArgsConstructor
public class OotdGetSimilarRes {

    private Long id;

    private String image;

    public OotdGetSimilarRes(OotdImage ootdImage) {
        this.id = ootdImage.getOotd().getId();
        this.image = ootdImage.getImageUrl();
    }
}
