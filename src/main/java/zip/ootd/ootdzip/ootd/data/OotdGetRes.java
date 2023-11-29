package zip.ootd.ootdzip.ootd.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;

@Data
public class OotdGetRes {

    private String contents;

    private boolean isLike;

    private int viewCount;

    private int reportCount;

    private int likeCount;

    private boolean isBookmark;

    private LocalDateTime createAt;

    private List<OotdImageRes> ootdImages;

    private List<OotdStyleRes> styles;

    public OotdGetRes(Ootd ootd,
            boolean isLike,
            boolean isBookmark,
            int viewCount,
            int likeCount) {

        this.isLike = isLike;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.isBookmark = isBookmark;

        this.reportCount = ootd.getReportCount();
        this.contents = ootd.getContents();
        this.createAt = ootd.getCreatedAt();

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

            private String name;

            private Category category;

            private String size;

            private String material;

            private String purchaseStore;

            private String purchaseDate;

            private Coordinate coordinate;

            private DeviceSize deviceSize;

            public OotdImageClothesRes(OotdImageClothes ootdImageClothes) {
                this.coordinate = ootdImageClothes.getCoordinate();
                this.deviceSize = ootdImageClothes.getDeviceSize();

                Clothes clothes = ootdImageClothes.getClothes();
                this.brand = new BrandRes(clothes.getBrand());
                this.name = clothes.getName();
                this.category = clothes.getCategory();
                this.size = clothes.getSize();
                this.material = clothes.getMaterial();
                this.purchaseStore = clothes.getPurchaseStore();
                this.purchaseDate = clothes.getPurchaseDate();
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
}
