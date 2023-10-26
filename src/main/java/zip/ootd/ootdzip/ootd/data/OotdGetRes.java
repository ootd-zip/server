package zip.ootd.ootdzip.ootd.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.domain.OotdImage;
import zip.ootd.ootdzip.ootdclothe.domain.OotdClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.UserGender;

@Data
public class OotdGetRes {

    private String contents;

    private boolean isLike;

    private int viewCount;

    private int reportCount;

    private int likeCount;

    private boolean isBookmark;

    private LocalDateTime createAt;

    private UserGender gender;

    private List<OotdImageRes> ootdImages;

    private List<OotdStyleRes> styles;

    private List<OotdClothesListRes> ootdClothesList;

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
        this.gender = ootd.getGender();
        this.createAt = ootd.getCreatedAt();

        this.styles = ootd.getStyles().stream()
                .map(OotdStyleRes::new)
                .collect(Collectors.toList());

        this.ootdImages = ootd.getOotdImages().stream()
                .map(OotdImageRes::new)
                .collect(Collectors.toList());
        this.ootdClothesList = ootd.getOotdClothesList().stream()
                .map(OotdClothesListRes::new)
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

    @Data
    static class OotdClothesListRes {

        private BrandRes brand;

        private String name;

        private Category category;

        private String size;

        private String material;

        private String purchaseStore;

        private String purchaseDate;

        public OotdClothesListRes(OotdClothes ootdClothes) {
            Clothes clothes = ootdClothes.getClothes();
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
