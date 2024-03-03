package zip.ootd.ootdzip.ootd.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.category.data.DetailCategory;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;

@Data
@NoArgsConstructor
public class OotdGetRes {

    private Long id;

    private String contents;

    private Boolean isLike;

    private Integer viewCount;

    private Integer reportCount;

    private Integer likeCount;

    private Boolean isBookmark;

    private String userName;

    private String userImage;

    private Integer userHeight;

    private Integer userWeight;

    private Long userId;

    private LocalDateTime createAt;

    private Boolean isFollowing;

    private Boolean isPrivate;

    private List<OotdImageRes> ootdImages;

    private List<OotdStyleRes> styles;

    public OotdGetRes(Ootd ootd,
            Boolean isLike,
            Integer viewCount,
            Integer likeCount,
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
        this.isPrivate = ootd.isPrivate();

        this.userId = ootd.getWriter().getId();
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

        private Long id;

        private String name;

        public OotdStyleRes(OotdStyle ootdStyle) {
            this.id = ootdStyle.getStyle().getId();
            this.name = ootdStyle.getStyle().getName();
        }
    }

    @Data
    static class OotdImageRes {

        private String ootdImage;

        private List<OotdImageClothesRes> ootdImageClothesList;

        public OotdImageRes(OotdImage ootdImage) {
            this.ootdImage = ootdImage.getImageUrl();
            this.ootdImageClothesList = ootdImage.getOotdImageClothesList().stream()
                    .map(OotdImageClothesRes::new)
                    .collect(Collectors.toList());
        }

        @Data
        static class OotdImageClothesRes {

            private BrandDto brand;

            private String clothesName;

            private String clothesImage;

            private DetailCategory category;

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
                this.clothesImage = clothes.getImageUrl();
                this.brand = BrandDto.of(clothes.getBrand());
                this.category = DetailCategory.builder()
                        .id(clothes.getCategory().getId())
                        .categoryName(clothes.getCategory().getName())
                        .parentCategoryName(clothes.getCategory().getParentCategory().getName())
                        .build();
                this.size = clothes.getSize().getName();
            }
        }
    }
}
