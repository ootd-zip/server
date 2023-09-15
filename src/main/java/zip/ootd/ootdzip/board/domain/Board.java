package zip.ootd.ootdzip.board.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.boardclothe.domain.BoardClothes;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boards")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    private String contents;

    @Builder.Default
    @Column(nullable = false)
    private Integer viewCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isBlocked = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer reportCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer likeCount = 0;

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardImage> boardImages = new ArrayList<>();

    @Builder.Default
    @ElementCollection(targetClass = Style.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "board_styles", joinColumns = @JoinColumn(name = "board_id"))
    @Enumerated(EnumType.STRING)
    private List<Style> styles = new ArrayList<>();

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Builder.Default
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<BoardClothes> boardClothesList = new ArrayList<>();

    public static Board createBoard(User user,
                                    String contents,
                                    UserGender gender,
                                    boolean isPublic,
                                    List<BoardImage> boardImages,
                                    List<BoardClothes> boardClothesList,
                                    List<Style> styles) {

        Board board = Board.builder()
                .writer(user)
                .gender(gender)
                .isPublic(isPublic)
                .contents(contents)
                .build();


        board.addBoardImages(boardImages);
        board.addBoardClothesList(boardClothesList);
        board.addStyles(styles);

        return board;
    }

    // == 연관관계 메서드 == //
    public void addBoardImage(BoardImage boardImage) {
        boardImages.add(boardImage);
        boardImage.setBoard(this);
    }

    public void addBoardImages(List<BoardImage> boardImages) {
        boardImages.forEach(this::addBoardImage);
    }

    public void addBoardClothes(BoardClothes boardClothes) {
        boardClothesList.add(boardClothes);
        boardClothes.setBoard(this);
    }

    public void addBoardClothesList(List<BoardClothes> boardClothesList) {
        boardClothesList.forEach(this::addBoardClothes);
    }

    public void addStyle(Style style) {
        styles.add(style);
    }

    public void addStyles(List<Style> styles) {
        styles.forEach(this::addStyle);
    }
}
