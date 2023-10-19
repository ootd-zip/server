package zip.ootd.ootdzip.board.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import zip.ootd.ootdzip.board.domain.Board;
import zip.ootd.ootdzip.board.domain.BoardImage;
import zip.ootd.ootdzip.boardclothe.domain.BoardClothes;
import zip.ootd.ootdzip.boardstyle.BoardStyle;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.user.domain.UserGender;

@Data
public class BoardOotdGetAllRes {

    private Long id;

    private boolean isLike;

    private boolean isBookMark;

    private String name;

    private String profileImage;

    private String contents;

    private int viewCount;

    private int reportCount;

    private int likeCount;

    private LocalDateTime createAt;

    private UserGender gender;

    private List<OotdBoardImage> boardImages;

    private List<OotdStyle> styles;

    private List<OotdBoardClothesList> boardClothesList;

    public BoardOotdGetAllRes(Board board,
                              boolean isLike,
                              boolean isBookmark,
                              int viewCount,
                              int likeCount) {

        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.isBookMark = isBookmark;

        this.id = board.getId();
        this.isLike = isLike;
        this.reportCount = board.getReportCount();
        this.contents = board.getContents();
        this.gender = board.getGender();
        this.createAt = board.getCreatedAt();

        this.name = board.getWriter().getName();
        this.profileImage = board.getWriter().getProfileImage();

        this.styles = board.getStyles().stream()
                .map(OotdStyle::new)
                .collect(Collectors.toList());

        this.boardImages = board.getBoardImages().stream()
                .map(OotdBoardImage::new)
                .collect(Collectors.toList());
        this.boardClothesList = board.getBoardClothesList().stream()
                .map(OotdBoardClothesList::new)
                .collect(Collectors.toList());
    }

    @Data
    static class OotdStyle {

        private String name;

        public OotdStyle(BoardStyle boardStyle) {
            this.name = boardStyle.getStyle().getName();
        }
    }

    @Data
    static class OotdBoardImage {

        private String url;

        public OotdBoardImage(BoardImage boardImage) {
            this.url = boardImage.getImageUrl();
        }
    }

    @Data
    static class OotdBoardClothesList {

        private BoardOotdGetRes.OotdBoardClothesList.ClothesBrand brand;

        private String name;

        private Category category;

        private String size;

        private String material;

        private String purchaseStore;

        private String purchaseDate;

        public OotdBoardClothesList(BoardClothes boardClothes) {
            Clothes clothes = boardClothes.getClothes();
            this.brand = new BoardOotdGetRes.OotdBoardClothesList.ClothesBrand(clothes.getBrand());
            this.name = clothes.getName();
            this.category = clothes.getCategory();
            this.size = clothes.getSize();
            this.material = clothes.getMaterial();
            this.purchaseStore = clothes.getPurchaseStore();
            this.purchaseDate = clothes.getPurchaseDate();
        }

        @Data
        static class ClothesBrand {

            private String name;

            public ClothesBrand(Brand brand) {
                this.name = brand.getName();
            }
        }
    }
}
