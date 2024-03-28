package zip.ootd.ootdzip.ootd.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class OotdSearchRes {
    private Long id;
    private String imageUrl;
    private Integer imageCount;

    public static OotdSearchRes of(Ootd ootd) {
        return OotdSearchRes.builder()
                .id(ootd.getId())
                .imageUrl(ootd.getFirstImage())
                .imageCount(ootd.getImageCount())
                .build();
    }
}
