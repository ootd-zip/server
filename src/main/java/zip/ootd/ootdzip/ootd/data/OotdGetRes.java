package zip.ootd.ootdzip.ootd.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;

@Data
public class OotdGetRes {

    private Long id;

    private String contents;

    private boolean isLike;

    private int viewCount;

    private int reportCount;

    private int likeCount;

    private boolean isBookmark;

    private String userName;

    private String userImage;

    private LocalDateTime createAt;

    private List<OotdImageRes> ootdImages;

    private List<OotdStyleRes> styles;

    private List<OotdComment> comment;

    public OotdGetRes(Ootd ootd,
            boolean isLike,
            boolean isBookmark,
            int viewCount,
            int likeCount) {

        this.isLike = isLike;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.isBookmark = isBookmark;

        this.id = ootd.getId();
        this.userName = ootd.getWriter().getName();
        this.userImage = ootd.getWriter().getProfileImage();
        this.reportCount = ootd.getReportCount();
        this.contents = ootd.getContents();
        this.createAt = ootd.getCreatedAt();

        this.styles = ootd.getStyles().stream()
                .map(OotdStyleRes::new)
                .collect(Collectors.toList());

        this.ootdImages = ootd.getOotdImages().stream()
                .map(OotdImageRes::new)
                .collect(Collectors.toList());

        this.comment = ootd.getComments().stream()
                .map(OotdComment::new)
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

        private List<OotdImageClothesRes> ootdImageClothesList;

        public OotdImageRes(OotdImage ootdImage) {
            this.url = ootdImage.getImageUrl();
            this.ootdImageClothesList = ootdImage.getOotdImageClothesList().stream()
                    .map(OotdImageClothesRes::new)
                    .collect(Collectors.toList());
        }

        @Data
        static class OotdImageClothesRes {

            private BrandRes brand;

            private String clothesName;

            private CategoryRes category;

            private Long clothesId;

            private String size;

            private Coordinate coordinate;

            private DeviceSize deviceSize;

            public OotdImageClothesRes(OotdImageClothes ootdImageClothes) {
                this.coordinate = ootdImageClothes.getCoordinate();
                this.deviceSize = ootdImageClothes.getDeviceSize();

                Clothes clothes = ootdImageClothes.getClothes();
                this.clothesId = clothes.getId();
                this.clothesName = clothes.getName();
                this.brand = new BrandRes(clothes.getBrand());
                this.category = new CategoryRes(clothes.getCategory());
                this.size = clothes.getSize().getName();
            }

            @Data
            static class CategoryRes {

                private String smallCategory;
                private String bigCategory;

                public CategoryRes(Category category) {
                    this.smallCategory = category.getName();
                    this.bigCategory = category.getParentCategory().getName();
                }
            }

            @Data
            static class BrandRes {

                private String name;

                public BrandRes(Brand brand) {
                    this.name = brand.getName();
                }
            }
        }
    }

    @Data
    static class OotdComment {

        private Long id;

        private String userName;

        private String userImage;

        private String content;

        private String timeStamp;

        List<ChildComment> childComment;

        public OotdComment(Comment comment) {
            this.id = comment.getId();
            this.userName = comment.getWriter().getName();
            this.content = comment.getContents();
            this.userImage = comment.getWriter().getProfileImage();
            this.timeStamp = comment.compareCreatedTimeAndNow();
            this.childComment = comment.getChildComments().stream()
                    .map(ChildComment::new)
                    .collect(Collectors.toList());
        }

        @Data
        static class ChildComment {

            private Long id;

            private String userName;

            private String userImage;

            private String content;

            private String timeStamp;

            private String taggedUserName;

            public ChildComment(Comment comment) {
                this.id = comment.getId();
                this.userName = comment.getWriter().getName();
                this.content = comment.getContents();
                this.userImage = comment.getWriter().getProfileImage();
                this.timeStamp = comment.compareCreatedTimeAndNow();
                if (comment.getTaggedUser() != null) {
                    this.taggedUserName = comment.getTaggedUser().getName();
                }
            }
        }
    }
}
