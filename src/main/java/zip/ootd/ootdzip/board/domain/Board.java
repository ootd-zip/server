package zip.ootd.ootdzip.board.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.boardbookmark.domain.BoardBookmark;
import zip.ootd.ootdzip.boardclothe.domain.BoardClothes;
import zip.ootd.ootdzip.boardlike.domain.BoardLike;
import zip.ootd.ootdzip.boardstyle.BoardStyle;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;

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
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLike> boardLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardBookmark> boardBookmarks = new ArrayList<>();

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
        return boardLikes.stream()
                .filter(bu -> Objects.equals(bu.getUser().getId(), id))
                .findAny()
                .map(BoardLike::isLike)
                .orElse(false);
    }

    public boolean changeUserLike(User user) {
        BoardLike boardLike = getBoardLikeOrMakeBoardLike(user);

        return boardLike.changeLike();
    }

    public void addBookmark(User user) {
        BoardBookmark boardBookmark = getBoardBookmark(user).orElse(BoardBookmark.createBoardBookmarkBy(user));
        boardBookmark.addBookmark();
        addBoardBookmark(boardBookmark);
    }

    public void cancelBookmark(User user) {
        BoardBookmark boardBookmark = getBoardBookmark(user).orElseThrow(NoSuchElementException::new);
        boardBookmark.cancelBookmark();
        deleteBoardBookmark(boardBookmark);
    }

    public boolean isBookmark(User user) {
        Optional<BoardBookmark> boardBookmark = getBoardBookmark(user);
        return boardBookmark.map(BoardBookmark::isBookmark).orElse(false);
    }

    private BoardLike getBoardLikeOrMakeBoardLike(User user) {
        Optional<BoardLike> boardLikeOptional = boardLikes.stream()
                .filter(bu -> Objects.equals(bu.getUser().getId(), user.getId()))
                .findAny();

        if (boardLikeOptional.isPresent()) {
            return boardLikeOptional.get();
        } else {
            BoardLike boardLike = BoardLike.createBoardLikeBy(user);
            addBoardLike(boardLike);
            return boardLike;
        }
    }

    private Optional<BoardBookmark> getBoardBookmark(User user) {
        return boardBookmarks.stream()
                .filter(bb -> Objects.equals(bb.getUser().getId(), user.getId()))
                .findAny();
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

    public void addBoardLike(BoardLike boardLike) {
        boardLikes.add(boardLike);
        boardLike.setBoard(this);
    }

    public void addBoardLikes(List<BoardLike> boardLikes) {
        boardLikes.forEach(this::addBoardLike);
    }

    public void addBoardBookmark(BoardBookmark boardBookmark) {
        boardBookmarks.add(boardBookmark);
        boardBookmark.setBoard(this);
    }

    public void deleteBoardBookmark(BoardBookmark boardBookmark) {
        boardBookmarks.remove(boardBookmark);
    }
}
