package zip.ootd.ootdzip.ootdbookmark.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import zip.ootd.ootdzip.ootdbookmark.domain.OotdBookmark;

@Data
@Builder
@AllArgsConstructor
public class OotdBookmarkGetAllRes {

    private Long ootdId;

    private Long ootdBookmarkId;

    private String ootdImage;

    public static OotdBookmarkGetAllRes of(OotdBookmark ootdBookmark) {
        return OotdBookmarkGetAllRes.builder()
                .ootdId(ootdBookmark.getOotd().getId())
                .ootdBookmarkId(ootdBookmark.getId())
                .ootdImage(ootdBookmark.getOotd().getOotdImages().get(0).getImageUrl())
                .build();
    }
}
