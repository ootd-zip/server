package zip.ootd.ootdzip.home.data;

import lombok.Data;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesImage;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.domain.OotdImage;

@Data
public class ClothesAndOotdsForHomeRes {

    private Long id;

    private String message;

    private String detailMessage;

    private TagType tagType;

    private String imageUrl;

    private ClothesAndOotdsForHomeRes() {
    }

    public ClothesAndOotdsForHomeRes(Clothes clothes, String message, String detailMessage) {
        this.id = clothes.getId();
        this.message = message;
        this.detailMessage = detailMessage;
        this.tagType = TagType.CLOTHES;
        this.imageUrl = clothes.getClothesImages().stream().findFirst().orElse(new ClothesImage()).getImageUrl();
    }

    public ClothesAndOotdsForHomeRes(Ootd ootd, String message, String detailMessage) {
        this.id = ootd.getId();
        this.message = message;
        this.detailMessage = detailMessage;
        this.tagType = TagType.OOTD;
        this.imageUrl = ootd.getOotdImages().stream().findFirst().orElse(new OotdImage()).getImageUrl();
    }
}
