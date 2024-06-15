package zip.ootd.ootdzip.ootdlike.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.ootdlike.domain.OotdLike;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class OotdLikeRes {
    private Long ootdId;
    private String ootdImageUrl;
    private Integer ootdImageCount;
    private Long writerId;
    private String writerProfileImage;
    private String writerName;

    public static OotdLikeRes of(OotdLike ootdLike) {
        return OotdLikeRes.builder()
                .ootdId(ootdLike.getOotd().getId())
                .ootdImageUrl(ootdLike.getOotd().getFirstImage())
                .ootdImageCount(ootdLike.getOotd().getImageCount())
                .writerId(ootdLike.getOotd().getWriter().getId())
                .writerProfileImage(ootdLike.getOotd().getWriter().getProfileImage().getImageUrlSmall())
                .writerName(ootdLike.getOotd().getWriter().getName())
                .build();
    }
}
