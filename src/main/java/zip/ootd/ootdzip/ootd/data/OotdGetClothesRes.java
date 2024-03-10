package zip.ootd.ootdzip.ootd.data;

import lombok.Data;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;

@Data
public class OotdGetClothesRes {

    private Long id;

    private String image;

    public OotdGetClothesRes(OotdImage ootdImage) {
        this.id = ootdImage.getOotd().getId();
        this.image = ootdImage.getImageUrl();
    }
}
