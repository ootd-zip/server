package zip.ootd.ootdzip.user.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.images.domain.Images;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.user.data.UserRole;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User extends BaseEntity {

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "followings")
    private final Set<User> followers = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "followers",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "follower_id")})
    private final Set<User> followings = new HashSet<>();

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    private UserGender gender = UserGender.UNKNOWN;

    private Integer age;

    private Integer height;

    private Integer weight;

    private Boolean isBodyPrivate = false;

    private Images images;

    private String description;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'USER'")
    private UserRole userRole = UserRole.USER;

    @OneToMany(mappedBy = "user")
    private List<Clothes> clothesList;

    @OneToMany(mappedBy = "writer")
    private List<Ootd> ootds;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserStyle> userStyles;

    public static User getDefault() {
        return User.builder()
                .name(null)
                .gender(null)
                .age(null)
                .height(0)
                .isBodyPrivate(false)
                .weight(0)
                .images(Images.defaultImage())
                .description(null)
                .isCompleted(false)
                .isDeleted(false)
                .clothesList(new ArrayList<>())
                .ootds(new ArrayList<>())
                .userStyles(new ArrayList<>())
                .build();
    }

    public boolean addFollower(User user) {
        return followers.add(user)
                && user.followings.add(this);
    }

    public boolean removeFollower(User user) {
        return followers.remove(user)
                && user.followings.remove(this);
    }

    public boolean isFollowing(User user) {
        return this.followings.contains(user);
    }

    public Integer getProfileHeight(User loginUser) {
        if (isBodyPrivate && !loginUser.equals(this)) {
            return 0;
        }

        return height;
    }

    public Integer getProfileWeight(User loginUser) {
        if (isBodyPrivate && !loginUser.equals(this)) {
            return 0;
        }

        return weight;
    }

    public Long getFollowerCount() {
        return followers
                .stream()
                .filter(x -> !x.getIsDeleted())
                .count();
    }

    public Long getFollowingCount() {
        return followings
                .stream()
                .filter(x -> !x.getIsDeleted())
                .count();
    }

    public Long getOotdsCount(User loginUser) {
        if (ootds == null) {
            return 0L;
        }

        return ootds
                .stream()
                .filter(x -> !x.getIsDeleted()
                        && !x.getIsBlocked()
                        && (x.getWriter().getId().equals(loginUser.getId()) || !x.isPrivate()))
                .count();
    }

    public Long getClothesCount(User loginUser) {
        if (clothesList == null) {
            return 0L;
        }

        return clothesList
                .stream()
                .filter(x -> x.getUser().getId().equals(loginUser.getId()) || !x.getIsPrivate())
                .count();
    }

    public void registerBy(String name,
            UserGender gender,
            Integer age,
            Integer height,
            Integer weight,
            Boolean isBodyPrivate,
            List<UserStyle> userStyles) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.isBodyPrivate = isBodyPrivate;
        this.isCompleted = true;
    }

    public void addUserStyle(UserStyle userStyle) {
        this.userStyles.add(userStyle);
    }

    public void updateProfile(String name,
            Images images,
            String description,
            Integer height,
            Integer weight,
            Boolean isBodyPrivate) {
        this.name = name;
        this.images = images;
        this.description = description;
        this.height = height;
        this.weight = weight;
        this.isBodyPrivate = isBodyPrivate;

    }

    public void disjoin() {
        this.isDeleted = true;
    }

    public String getName() {
        if (isDeleted) {
            return "알수없음";
        }
        return name;
    }

    public Images getImages() {
        if (isDeleted) {
            throw new CustomException(ErrorCode.DELETED_USER_ERROR);
        }
        return images;
    }
}
