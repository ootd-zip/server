package zip.ootd.ootdzip.board.domain;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.boardclothe.domain.BoardClothes;
import zip.ootd.ootdzip.boardstyle.BoardStyle;
import zip.ootd.ootdzip.boarduser.domain.BoardUser;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "boards")
@Getter
@Setter
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
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardStyle> styles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardUser> boardUsers = new ArrayList<>();

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
                                    List<BoardStyle> boardStyles) {

        Board board = Board.builder()
                .writer(user)
                .gender(gender)
                .isPublic(isPublic)
                .contents(contents)
                .build();


        board.addBoardImages(boardImages);
        board.addBoardClothesList(boardClothesList);
        board.addBoardStyles(boardStyles);

        return board;
    }

    public boolean isUserLike(Long id) {
        return boardUsers.stream()
                .filter(bu -> Objects.equals(bu.getUser().getId(), id))
                .findAny()
                .map(BoardUser::isLike)
                .orElse(false);
    }

    public boolean changeUserLike(User user) {
        Optional<BoardUser> boardUserOptional = boardUsers.stream()
                .filter(bu -> Objects.equals(bu.getUser().getId(), user.getId()))
                .findAny();

        boolean result;
        if (boardUserOptional.isPresent()) {
            result = boardUserOptional.get().changeLike();
        } else {
            BoardUser boardUser = BoardUser.createBoardUserBy(user);
            addBoardUser(boardUser);
            result = boardUser.changeLike();
        }
        return result;
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

    public void addBoardStyle(BoardStyle boardStyle) {
        styles.add(boardStyle);
        boardStyle.setBoard(this);
    }

    public void addBoardStyles(List<BoardStyle> boardStyles) {
        boardStyles.forEach(this::addBoardStyle);
    }

    public void addBoardUser(BoardUser boardUser) {
        boardUsers.add(boardUser);
        boardUser.setBoard(this);
    }

    public void addBoardUsers(List<BoardUser> boardUsers) {
        boardUsers.forEach(this::addBoardUser);
    }
}
