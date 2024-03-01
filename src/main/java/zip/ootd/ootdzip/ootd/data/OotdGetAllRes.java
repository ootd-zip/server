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

    private boolean isLike;

    private boolean isBookmark;

    private String userName;

    private String userImage;

    private String userHeight;

    private String userWeight;

    private String contents;

    private int viewCount;

    private int reportCount;

    private int likeCount;

    private boolean isFollowing;

    private LocalDateTime createAt;

    private List<OotdImageRes> ootdImages;

    private List<OotdStyleRes> styles;

    public OotdGetAllRes(Ootd ootd,
            boolean isLike,
            int viewCount,
            int likeCount,
            User loginUser) {

        this.isLike = isLike;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.isBookmark = ootd.isBookmark(loginUser);
        this.isFollowing = loginUser.isFollowing(ootd.getWriter());

        this.id = ootd.getId();
        this.reportCount = ootd.getReportCount();
        this.contents = ootd.getContents();
        this.createAt = ootd.getCreatedAt();

        this.userName = ootd.getWriter().getName();
        this.userImage = ootd.getWriter().getProfileImage();
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
            this.url = ootdImage.getImageUrl();
        }
    }
}
