package zip.ootd.ootdzip.user.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.ootd.domain.Ootd;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
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
    private LocalDate birthdate;
    private Integer height;
    private Boolean showHeight;
    private Integer weight;
    private Boolean showWeight;
    @Column(length = 2048)
    private String profileImage;
    private String description;
    @Column(nullable = false)
    private Boolean isCompleted = false;
    @Column(nullable = false)
    private Boolean isDeleted = false;
    @OneToMany(mappedBy = "user")
    private List<Clothes> clothesList;
    @OneToMany(mappedBy = "writer")
    private List<Ootd> ootds;

    @Builder
    private User(String name, UserGender gender, LocalDate birthdate, Integer height, Boolean showHeight,
            Integer weight,
            Boolean showWeight, String profileImage, String description, Boolean isCompleted, Boolean isDeleted,
            List<Clothes> clothesList, List<Ootd> ootds) {
        this.name = name;
        this.gender = gender;
        this.birthdate = birthdate;
        this.height = height;
        this.showHeight = showHeight;
        this.weight = weight;
        this.showWeight = showWeight;
        this.profileImage = profileImage;
        this.description = description;
        this.isCompleted = isCompleted;
        this.isDeleted = isDeleted;
        this.clothesList = clothesList;
        this.ootds = ootds;
    }

    public static User getDefault() {
        return User.builder()
                .name(null)
                .gender(null)
                .birthdate(null)
                .height(0)
                .showHeight(true)
                .weight(0)
                .showWeight(true)
                .profileImage(null)
                .description(null)
                .isCompleted(false)
                .isDeleted(false)
                .clothesList(null)
                .ootds(null)
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

    public String getProfileHeight(User loginUser) {
        if (!showHeight && !loginUser.equals(this)) {
            return "비공개";
        }

        return height + "cm";
    }

    public String getProfileWeight(User loginUser) {
        if (!showWeight && !loginUser.equals(this)) {
            return "비공개";
        }

        return weight + "kg";
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

}
