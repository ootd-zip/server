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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.common.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDate birthdate;
    private Integer height;
    private Boolean showHeight;
    private Integer weight;
    private Boolean showWeight;
    @Column(length = 2048)
    private String profileImage;
    @Column(nullable = false)
    private Boolean isCompleted = false;
    @Column(nullable = false)
    private Boolean isDeleted = false;
    @OneToMany(mappedBy = "user")
    private List<Clothes> clothesList;

    public static User getDefault() {
        return new User(
                null,
                null,
                null,
                0,
                false,
                0,
                false,
                null,
                false,
                false,
                null);
    }

    public boolean addFollower(User user) {
        return followers.add(user)
                && user.followings.add(this);
    }

    public boolean removeFollower(User user) {
        return followers.remove(user)
                && user.followings.remove(this);
    }
}
