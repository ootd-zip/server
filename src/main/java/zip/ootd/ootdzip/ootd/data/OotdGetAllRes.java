package zip.ootd.ootdzip.ootd.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;

@Data
public class OotdGetAllRes {

    private Long id;

    private Boolean isLike;

    private Boolean isBookmark;

    private String userName;

    private String userImage;

    private Integer userHeight;

    private Integer userWeight;

    private String contents;

    private Integer viewCount;

    private Integer reportCount;

    private Integer likeCount;

    private Boolean isFollowing;

    private LocalDateTime createAt;

    private List<OotdImageRes> ootdImages;

    private List<OotdStyleRes> styles;

    public OotdGetAllRes(Ootd ootd, User loginUser) {

        this.isLike = ootd.isOotdLike(loginUser);
        this.viewCount = ootd.getViewCount();
        this.likeCount = ootd.getLikeCount();
        this.isBookmark = ootd.isBookmark(loginUser);
        this.isFollowing = loginUser.isFollowing(ootd.getWriter());

        this.id = ootd.getId();
        this.reportCount = ootd.getReportCount();
        this.contents = ootd.getContents();
        this.createAt = ootd.getCreatedAt();

        this.userName = ootd.getWriter().getName();
        this.userImage = ootd.getWriter().getProfileImage().getImageUrlSmall();
        this.userHeight = ootd.getWriter().getProfileHeight(loginUser);
        this.userWeight = ootd.getWriter().getProfileWeight(loginUser);

        this.styles = ootd.getStyles().stream()
                .map(OotdStyleRes::new)
                .collect(Collectors.toList());

        this.ootdImages = ootd.getOotdImages().stream()
                .map(OotdImageRes::new)
                .collect(Collectors.toList());
    }

    @Data
    static class OotdStyleRes {

        private String name;

        public OotdStyleRes(OotdStyle ootdStyle) {
            this.name = ootdStyle.getStyle().getName();
        }
    }

    @Data
    static class OotdImageRes {

        private String url;

        public OotdImageRes(OotdImage ootdImage) {
            this.url = ootdImage.getImages().getImageUrlBig();
        }
    }
}
