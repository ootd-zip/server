package zip.ootd.ootdzip.ootdbookmark.data;

import lombok.Data;
import zip.ootd.ootdzip.ootdbookmark.domain.OotdBookmark;

@Data
public class OotdBookmarkGetAllRes {

    private Long ootdId;

    private Long ootdBookmarkId;

    private String ootdImage;

    public OotdBookmarkGetAllRes(OotdBookmark ootdBookmark) {
        this.ootdId = ootdBookmark.getOotd().getId();
        this.ootdBookmarkId = ootdBookmark.getId();
        this.ootdImage = ootdBookmark.getOotd().getOotdImages().get(0).getImageUrl();
    }
}
