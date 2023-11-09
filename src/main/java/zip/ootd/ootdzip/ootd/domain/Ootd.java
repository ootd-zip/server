package zip.ootd.ootdzip.ootd.domain;

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
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.ootdbookmark.domain.OotdBookmark;
import zip.ootd.ootdzip.ootdlike.domain.OotdLike;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;

@Entity
@Table(name = "ootds")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ootd extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
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
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL)
    private List<OotdImage> ootdImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL)
    private List<OotdStyle> styles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OotdLike> ootdLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ootd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OotdBookmark> ootdBookmarks = new ArrayList<>();

    @Column(nullable = false)
    private boolean isPrivate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserGender gender;

    public static Ootd createOotd(User user,
            String contents,
            UserGender gender,
            boolean isPrivate,
            List<OotdImage> ootdImages,
            List<OotdStyle> ootdStyles) {

        Ootd ootd = Ootd.builder()
                .writer(user)
                .gender(gender)
                .isPrivate(isPrivate)
                .contents(contents)
                .build();

        ootd.addOotdImages(ootdImages);
        ootd.addOotdStyles(ootdStyles);

        return ootd;
    }

    public void updateViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void updateLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void addLike(User user) {
        OotdLike ootdLike = getOotdLike(user).orElse(OotdLike.createOotdLikeBy(user));
        addOotdLike(ootdLike);
    }

    public void cancelLike(User user) {
        OotdLike ootdLike = getOotdLike(user).orElseThrow(NoSuchElementException::new);
        deleteOotdLike(ootdLike);
    }

    public boolean isOotdLike(User user) {
        Optional<OotdLike> ootdLike = getOotdLike(user);
        return ootdLike.isPresent();
    }

    private Optional<OotdLike> getOotdLike(User user) {
        return ootdLikes.stream()
                .filter(bl -> Objects.equals(bl.getUser().getId(), user.getId()))
                .findAny();
    }

    public void addBookmark(User user) {
        OotdBookmark ootdBookmark = getOotdBookmark(user).orElse(OotdBookmark.createOotdBookmarkBy(user));
        addOotdBookmark(ootdBookmark);
    }

    public void cancelBookmark(User user) {
        OotdBookmark ootdBookmark = getOotdBookmark(user).orElseThrow(NoSuchElementException::new);
        deleteOotdBookmark(ootdBookmark);
    }

    public boolean isBookmark(User user) {
        Optional<OotdBookmark> ootdBookmark = getOotdBookmark(user);
        return ootdBookmark.isPresent();
    }

    private Optional<OotdBookmark> getOotdBookmark(User user) {
        return ootdBookmarks.stream()
                .filter(bb -> Objects.equals(bb.getUser().getId(), user.getId()))
                .findAny();
    }

    // == 연관관계 메서드 == //
    public void addOotdImage(OotdImage ootdImage) {
        ootdImages.add(ootdImage);
        ootdImage.setOotd(this);
    }

    public void addOotdImages(List<OotdImage> ootdImages) {
        ootdImages.forEach(this::addOotdImage);
    }

    public void addOotdStyle(OotdStyle ootdStyle) {
        styles.add(ootdStyle);
        ootdStyle.setOotd(this);
    }

    public void addOotdStyles(List<OotdStyle> ootdStyles) {
        ootdStyles.forEach(this::addOotdStyle);
    }

    public void addOotdLike(OotdLike ootdLike) {
        ootdLikes.add(ootdLike);
        ootdLike.setOotd(this);
    }

    public void deleteOotdLike(OotdLike ootdLike) {
        ootdLikes.remove(ootdLike);
    }

    public void addOotdBookmark(OotdBookmark ootdBookmark) {
        ootdBookmarks.add(ootdBookmark);
        ootdBookmark.setOotd(this);
    }

    public void deleteOotdBookmark(OotdBookmark ootdBookmark) {
        ootdBookmarks.remove(ootdBookmark);
    }
}
